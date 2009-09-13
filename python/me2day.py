#
# -*- coding: utf-8 -*-
# 
# Author: Jang-Ho Hwang <rath@xrath.com>
# Version: 1.0, since 2009/09/13
#
import httplib, urllib
import base64, md5, random
from datetime import datetime

import libxml2

class Post:
    def __init__(self):
        self.id = None
        self.permalink = None
        self.author = None
        self.body = None
        self.body_as_text = None
        self.metoo_count = 0
        self.comment_count = 0
        self.tags = []
        self.date = None
    def __str__(self):
        return "<me2day.Post#%s, %s>" % (self.id, self.author.id)

class Person:
    def __init__(self):
        self.id = None
        self.nickname = None
        self.me2day_home = None
        self.openid = None 
        self.description = None
        self.face = None
        self.homepage = None
        self.friend_count = 0
        self.updated = None
    def __str__(self):
        return "<me2day.Person#%s, %s>" % (self.id, self.nickname)

class Comment:
    def __init__(self):
        self.id = None
        self.author = None
        self.body = None
        #self.body_as_text = None
        self.date = None
    def __str__(self):
        return "<me2day.Comment#%s, %s at %s>" % (self.id, self.author.id, self.date)

"""
    me2DAY API wrapper.

    TODO: The current implementation doesn't support TimeZone on each Post, Comment instance.
    TODO: To handle exceptional i/o error.
"""
class me2API:
    HOST = 'me2day.net'

    def __init__(self, username=None, userkey=None, app_key=None):
        self.username = username
        self.headers = {}
        if app_key:
            self.headers['me2_application_key'] = app_key
        if username and userkey:
            self.headers['Authorization'] = "Basic %s" % self._create_auth(username,userkey)

    def _create_auth(self, id, pw):
        nonce = md5.md5((random.random()*(10**10)).__str__()).hexdigest()[:8]
        return base64.b64encode("%s:%s%s" % (id, nonce, md5.md5(nonce+pw).hexdigest()))

    def _get(self, uri, processor=None, params={}):
        con = httplib.HTTPConnection(self.HOST)
        ret = None
        try:
            con.request("GET", uri, urllib.urlencode(params), self.headers)
            res = con.getresponse()
            if res.status/100==4:
                raise Error("%d %s" % (res.status, res.reason))
            data = res.read()
            if processor:
                doc = libxml2.parseDoc(data)
                ctx = doc.xpathNewContext()
                try:
                    ret = processor(ctx)
                finally:
                    ctx.xpathFreeContext()
                    doc.freeDoc()
        finally:
            con.close()
        return ret

    def _post(self, uri, processor=None, params={}):
        con = httplib.HTTPConnection(self.HOST)
        head = self.headers
        head['Content-Type'] = 'application/x-www-form-urlencoded'

        ret = None
        try:
            con.request("POST", uri, urllib.urlencode(params), head)
            res = con.getresponse()
            if res.status/100==4:
                raise Error("%d %s" % (res.status, res.reason))
            data = res.read()
            if processor:
                doc = libxml2.parseDoc(data)
                ctx = doc.xpathNewContext()
                try:
                    ret = processor(ctx)
                finally:
                    ctx.xpathFreeContext()
                    doc.freeDoc()
        finally:
            con.close()
        return ret
        
    def _process_get_posts(self, ctx):
        posts = ctx.xpathEval("//post")

        ret = []
        f = lambda post, eval: post.xpathEval(eval + "/text()")[0].content
        for post in posts:
            p = Post()
            p.id = f(post, "./post_id")
            p.permalink = f(post, "./permalink")
            p.body = f(post, "./body")
            p.body_as_text = f(post, "./textBody")
            p.metoo_count = int(f(post, "./metooCount"))
            p.comment_count = int(f(post, "./commentsCount"))
            p.date = datetime.strptime(f(post, "./pubDate")[:-5], "%Y-%m-%dT%H:%M:%S")
            p.tags = map(lambda x: x.content, post.xpathEval("./tags/tag/name/text()"))
            
            author = Person()
            author.id = f(post, "./author/id")
            author.nickname = f(post, "./author/nickname")
            author.me2day_home = f(post, "./author/me2dayHome")
            author.face = f(post, "./author/face")
            p.author = author
            ret.append(p)
        return ret

    def _process_get_comments(self, ctx):
        comments = ctx.xpathEval("//comment")
        ret = []
        f = lambda post, eval: post.xpathEval(eval + "/text()")[0].content
        for cmt in comments:
            c = Comment()
            c.id = f(cmt, "./commentId")
            c.body = f(cmt, "./body")
            c.date = datetime.strptime(f(cmt, "./pubDate")[:-5], "%Y-%m-%dT%H:%M:%S")

            author = Person()
            author.id = f(cmt, "./author/id")
            author.nickname = f(cmt, "./author/nickname")
            author.face = f(cmt, "./author/face")
            author.me2day_home = f(cmt, "./author/me2dayHome")
            c.author = author
            ret.append(c)
        return ret

    def _process_get_friends(self, ctx):
        friends = ctx.xpathEval("//person")
        ret = []
        f = lambda p, eval: p.xpathEval(eval + "/text()")[0].content
        for friend in friends:
            p = Person()    
            p.id = f(friend, "./id")
            p.nickname = f(friend, "./nickname")
            p.face = f(friend, "./face")
            p.me2day_home = f(friend, "./me2dayHome")
            p.friends_count = f(friend, "./friendsCount")
            p.updated = f(friend, "./updated")
            ret.append(p)
        return ret

    def _process_get_metoos(self, ctx):
        metoos = ctx.xpathEval("//metoo")
        ret = []
        f = lambda p, eval: p.xpathEval(eval + "/text()")[0].content
        for metoo in metoos:
            p = Person()
            p.id = f(metoo, "./author/id")
            p.nickname = f(metoo, "./author/nickname")
            p.face = f(metoo, "./author/face")
            p.me2day_home = f(metoo, "./author/me2dayHome")
            ret.append( (p, f(metoo, "./pubDate")) )
        return ret

    def _process_metoo(self, ctx):
        e = ctx.xpathEval("//error")
        f = lambda p, eval: p.xpathEval(eval + "/text()")[0].content
        return (int(f(e, "./code")), f(e, "./message"), f(e, "./description"))

    def get_posts(self, params={}, username=None):
        if not username:
            username = self.username
        querystring = urllib.urlencode(params)
        return self._get("/api/get_posts/%s.xml?%s" % (username, querystring), 
            self._process_get_posts)

    def get_comments(self, params={}, username=None):
        if not username:
            username = self.username
        querystring = urllib.urlencode(params)
        return self._get("/api/get_comments/%s.xml?%s" % (username, querystring),
            self._process_get_comments)

    def create_comment(self, params={}, username=None):
        """
        Parameters 'post_id' and 'body' must be set before invoke this method. 
        """
        if not username and not self.username:
            raise Error('username and userkey must be set')
        return self._post("/api/create_comment.xml", None, params)

    def create_post(self, params={}, username=None):
        """
        Supported parameters:
        * post[body] - a body of the post.
        * post[tags] - tags separated by space(0x20).
        * post[icon] - icon index between 1 and 12.
        * close_comment - set true if you don't want to be welcome any comments.
        * receive_sms - set true if you want to receive sms when other users add a comment.
        """
        if not username and not self.username:
            raise Error('username and userkey must be set')
        return self._post("/api/create_post.xml", None, params)

    def get_friends(self, params={}, username=None):
        """
        Supported parameters:
        * scope=all - All of friends
        * scope=close - Friends who have close relationship.
        * scope=supporter - Supporters up to 30. (auth required)
        * scope=sms - Interesting friends up to 30. (auth required)
        * scope=family - Friends who invite me, who was invited by me.
        * scope=mytag[_tagname_] - (auth required)
        * scope=group[_name_] - (auth required)
        """
        if not username:
            username = self.username
        querystring = urllib.urlencode(params)
        return self._get("/api/get_friends/%s.xml?%s" % (username, querystring), 
            self._process_get_friends)

    def get_metoos(self, params={}):
        """
        Get a list of persons who clicked metoo on a specific post.
        * post_id: id of the post.
        """
        querystring = urllib.urlencode(params)
        return self._get("/api/get_metoos.xml?%s" % querystring, self._process_get_metoos)

    def metoo(self, params={}, username=None):
        """
        Submit a metoo to a specific post.
        * post_id: id of the post.
        """
        if not username and not self.username:
            raise Error('username and userkey must be set')
        querystring = urllib.urlencode(params)
        return self._get("/api/metoo.xml?%s" % querystring, self._process_metoo)

    def noop(self):
        """
        No operation. but test your authentication.
        """
        return self._get("/api/noop.xml")

class Error(Exception):
    pass

