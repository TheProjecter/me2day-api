http://me2day.net/ API wrapper for various programming language.
It provides API set to create a new post/comment and fetch other useful information.

First release includes Java (1.5 or greater) and ActionScript 3.0.
Let's try as the following java code!

```
Post post = new Post();
post.setBody("Hey, check this pics");
post.setIconIndex(Post.ICON_THINK);
post.setTags("me2photo blah");

VirtualFile file = VirtualFile.create(new File("oops.png"));
file.setProgressListener( new ProgressListener() { 
  @Override public void transferProgress(ProgressEvent e) { 
    System.out.println( String.format("%d / %d", e.getTransferredBytes(), e.getTotalBytes()) );
  }});
post.setAttachment( file );


Me2API me2 = new Me2API()
me2.setUsername("rath")
me2.setUserKey("00112233")
me2.setApplicationKey("1")
me2.post(post);
```

## me2day api for python 0.1 ##
  * simplejson에 의존성이 있습니다. Python 2.6을 사용하시는 분은 me2day.py 상단의 import simplejson 구문을 import json으로 변경하여 의존성을 없앨 수 있습니다.
```
from me2day import me2API

#api = me2API('_username_', '_userkey_', '_application_key_') 
api = me2API('rath')
for f in api.get_friends():
  print "%s(%s)" % (f.id, f.nickname)

#api.create_comment({'post_id': 'p2mjxx', 'body': 'just for test'})

for p in api.get_posts({'scope': 'friend[close]', 'count': 20}):
  print p.id + ", " + p.author.nickname + ": " + p.body_as_text 

  for metoo in api.get_metoos({'post_id': p.id}):
    person, date = metoo
    print date + ", " + person.nickname

  for comment in api.get_comments({'post_id': p.id}):
    print "  %s, %s: %s" % (comment.id, comment.author.nickname, comment.body)

  api.noop()
```

## me2day api for java 0.6 ##
  * **IMPORTANT** 패키지 이름이 net.me2day.java에서 net.me2day로 변경되었습니다. 기존 매쉬업 개발자들은 소스코드를 수정해주셔야 합니다.
  * Comment, Icon, Metoo, Person, Post, TrackComment 클래스가 entity 패키지로 이동되었습니다. net.me2day.entity.Comment 이런식입니다.
  * 위의 Entity 클래스들을 GWT 환경에서 사용할 수 있도록 GWT Module 파일이 추가되고 net.me2day.gwt.client 패키지에 GWT 호환 entity 클래스들이 추가되었습니다. Me2API로 생성한 entity 클래스들을 GWT 호환 클래스로 변경하려면 기존 entity 클래스에 toGWT() 인터페이스를 통해 GWT 호환 오브젝트로 clone 할 수 있습니다. 각 entity 클래스들은 GWTFriendly 라는 인터페이스를 구현하게 되었으며 이 인터페이스에는 toGWT()만이 존재합니다.
  * GWT 에서 사용할 수 있도록 jar 파일에 entity 클래스들의 소스코드가 포함되도록 Ant Build 파일이 변경되었습니다.

## me2day api for java 0.5 ##
  * Me2API.post에서 150자 체크시 Textile link를 길이 체크에서 제외하도록 변경함 (thanks to [꼬룸](http://me2day.net/pragmatic))
  * 파일 첨부 지원 (thanks to [꼬룸](http://me2day.net/pragmatic))
  * Post 클래스에 위도/경도를 지정할 수 있도록 변경됨. (thanks to [꼬룸](http://me2day.net/pragmatic))

## me2day api for java 0.4 ##
  * Me2API.~~KIND\_XXX~~ 시리즈 필드가 **제거되었음**. Me2API.ICON\_XXX 시리즈로 교체 필요.
  * 2008.07.07일 기준, http://codian.springnote.com/pages/89009 에 명시된 api 모두 구현.
    * track\_comments 구현. Me2API.getCommentsByMe 와 Me2API.getCommentsToMe
    * Me2API.metoo 추가
    * Me2API.getMetoos 추가
    * Me2API.deleteComment 추가
    * 상위 메서드 구현을 위해 TrackComment 와 Metoo 클래스 추가
  * me2api application-key 발급을 위해 Me2API.setApplicationKey 의 method-doc 수정.
  * 프리미엄 제휴 파트너의 create\_post 지원을 위해 Me2API.post 메서드 파라미터에 Map 추가. (icon\_url, callback\_url, content\_type 등 지정 가능)

## me2api-ruby ##
A Ruby wrapper for the Me2day API.

  * [Project Home](https://github.com/me2day/me2api-ruby)