package net.me2day.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.security.MessageDigest;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.*;

import net.me2day.java.util.BASE64;
/**
 * 미투데이(http://me2day.net)에 글을 포스트할 수 있게 간단히 wrapping 한
 * 클래스입니다.
 * <p>
 * 자세한 사항은 <a href="http://codian.springnote.com/pages/86001">codian님의 스프링노트</a>를 보시기 바랍니다.
 * <p>
 * 간단한 사용 예는 다음과 같습니다.
 * <pre><font face="돋움체" color="#336699">
 * Me2API me2 = new Me2API();
 * me2.setUsername("rath");
 * me2.setUserKey("00112233"); 
 * me2.setApplicationKey("000000000000"); 
 * me2.post("아무리 할 일이 많아도 여유를 잃은 삶 따위는 살지 않으리.", "*활짝*", KIND_THINK);
 * </font></pre>
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/04/07
 */
public class Me2API
{
	public static final int KIND_THINK = 1;
	public static final int KIND_FEELING = 2;
	public static final int KIND_NOTICE = 3;

	private static final SimpleDateFormat fmtDate = 
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private Random rng = new Random(System.currentTimeMillis());

	private String username;
	private String userKey;
	private String appKey;
	private boolean subscribeSMS = false;

	private DocumentBuilder docBuilder;
	private String lastResult = null;

	public Me2API() throws ParserConfigurationException
	{
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();	
	}

	/**
	 * 미투데이 사용자 이름을 지정합니다.
	 */
	public void setUsername( String username )
	{
		this.username = username;
	}

	/**
	 * 미투데이 사용자 이름을 가져옵니다.
	 */
	public String getUsername()
	{
		return this.username;
	}

	/**
	 * 새 포스트 작성시 댓글수신 알람 SMS를 받을 것인지 말 것인지 설정한다. 
	 */
	public void setSubscribeSMS( boolean use )
	{
		this.subscribeSMS = use;	
	}

	/**
	 * 새 포스트 작성시 댓글을 수신받도록 설정한 결과를 가져온다.
	 */
	public boolean getSubscribeSMS()
	{
		return this.subscribeSMS;
	}

	/**
	 * 00112233 형식의 미투데이 <b>사용자 키</b>를 지정합니다.
	 * 미투데이 관리 페이지(http://me2day.net/yourname/setting)에서 가져올 수 있습니다.	 * 
	 */
	public void setUserKey( String userKey )
	{
		this.userKey = userKey;
	}

	/**
	 * 미투데이 사용자 키를 가져옵니다.
	 */
	public String getUserKey()
	{
		return this.userKey;
	}

	/**
	 * me2API 어플리케이션 키를 설정합니다. 지금은 별도의 발급 프로세스가 없으므로,
	 * 아무거나 입력하셔서 테스트 하실 수 있으며, 추후 재공지 하겠습니다. :)
	 */
	public void setApplicationKey( String appKey )
	{
		this.appKey = appKey;
	}

	/**
	 * 설정해둔 me2API 어플리케이션 키를 가져옵니다.
	 */
	public String getApplicationKey()
	{
		return this.appKey;
	}

	private String getHexaDecimal( byte[] b )
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<b.length; i++)
		{
			int v = (int)b[i];
			if( v < 0 )
				v += 0x100;
			String s = Integer.toHexString(v);
			if( s.length()==1 )
				sb.append('0');
			sb.append(s);
		}
		return sb.toString();
	}

	private String generateNonce()
	{
		byte[] buf = new byte[4];
		rng.nextBytes(buf);
		return getHexaDecimal(buf);
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 
	 * 이 메서드를 사용할 경우 태그는 붙지 않고 분류 아이콘은 '생각'으로 지정됩니다.
	 * 
	 * @param msg - 남기고자 하는 글.
	 */ 
	public void post( String msg )
		throws IOException
	{
		post( msg, "", KIND_THINK );
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 
	 * 이 메서드를 사용할 경우 태그는 붙지 않습니다.
	 *
	 * @param msg - 남기고자 하는 글.
	 * @param kind - KIND_THINK, KIND_FEELING, KIND_NOTICE 중에 하나.
	 */ 
	public void post( String msg, int kind )
		throws IOException
	{
		post( msg, "", kind );
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 
	 * 이 메서드를 사용할 경우 분류 아이콘은 '생각'이 됩니다.
	 *
	 * @param msg - 남기고자 하는 글.
	 * @param tags - 태그. 태그가 여러개일 경우 공백으로 구분해 넣어야 함.
	 */ 
	public void post( String msg, String tags )
		throws IOException
	{
		post( msg, tags, KIND_THINK );
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 
	 * 이 메서드를 사용할 경우 분류 아이콘은 '생각'이 됩니다.
	 *
	 * @param msg - 남기고자 하는 글.
	 * @param tags - 태그. 태그가 여러개일 경우 공백으로 구분해 넣어야 함.
	 * @param kind - KIND_THINK, KIND_FEELING, KIND_NOTICE 중에 하나.
	 */ 
	public void post( String msg, String tags, int kind )
		throws IOException
	{
		// 150자 길이체크는 나중에 넣자. URL 체크하기 귀찮다 -_-
		if( kind < 1 || kind > 3 )
			throw new IllegalArgumentException( "Kind must be between 1 and 3");
		if( username==null )
			throw new IllegalStateException("username cannot be null");
		if( userKey==null )
			throw new IllegalStateException("userKey cannot be null");
		/*
		if( appKey==null )
			throw new IllegalStateException("appKey cannot be null");
		*/
		
		URL url = new URL( "http://me2day.net/api/create_post" );
		Map<String, String> params = new HashMap<String, String>();
		params.put( "post[body]", msg );
		params.put( "post[tags]", tags );
		params.put( "post[kind]", String.valueOf(kind) );
		params.put( "receive_sms", String.valueOf(subscribeSMS) );

		request(url, "POST", params);
	}

	private String createPassword()
	{
		String nonce = generateNonce();
		String digest = null;
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			md.update( nonce.getBytes() );
			md.update( this.userKey.getBytes() );
			digest = getHexaDecimal(md.digest());
		}
		catch( Exception e )
		{
			System.err.println( "Warning: No provider for MD5 Digest exists!" );
		}

		return nonce + digest;
	}

	/**
	 * 문서화 할 이유가 없습니다. -.-
	 */
	protected Document request( URL url, String method, Map<String, String> params )
		throws IOException
	{
		Document ret = null;
		

		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod(method);

		if( this.userKey!=null )
		{
			String authKey = String.format("Basic %s", 
				new BASE64(false).encode(username + ":" + createPassword()));
			con.setRequestProperty("Authorization", authKey);
		}

		if( this.appKey!=null )
			con.setRequestProperty("me2_application_key", this.appKey);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setDoInput(true);
		
		if( method.equals("POST") )
		{
			con.setDoOutput(true);
			String paramstr = buildParameters(params);
			OutputStream out = con.getOutputStream();
			out.write( paramstr.getBytes("UTF-8") );
			out.flush();
			out.close();
		}

		InputStream in = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		try
		{
			in = con.getInputStream();
			while(true)
			{
				int readlen = in.read(buf);
				if( readlen < 1 )
					break;
				bos.write(buf, 0, readlen);	
			}
			String output = new String(bos.toByteArray(), "UTF-8");
			//System.out.println(output);

			this.lastResult = output;
			try
			{
				ret = docBuilder.parse(new InputSource(new StringReader(output)));
			}
			catch( SAXException ex )
			{
				this.lastResult = ex.toString();
			}
		}
		catch( IOException e )
		{
			if( con.getResponseCode()==500 )
			{
				bos.reset();
				InputStream err = con.getErrorStream();
				while(true)
				{
					int readlen = err.read(buf);
					if( readlen < 1 )
						break;
					bos.write(buf, 0, readlen);
				}
				String output = new String(bos.toByteArray(), "UTF-8");
				System.err.println(output);
			}
			throw e;
		}
		finally
		{
			if( in!=null )
				in.close();
			if( con!=null )
				con.disconnect();
		}
		return ret;
	}

	/**
	 * 주어진 Map의 key/value를 application/x-www-form-urlencoded 형식으로 만들어줍니다.
	 */
	protected String buildParameters( Map<String, String> params )
		throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for(Iterator i= params.keySet().iterator(); i.hasNext(); )
		{
			String key = (String)i.next();
			sb.append( key );
			sb.append( "=" );
			sb.append( URLEncoder.encode(params.get(key), "UTF-8") );
			if( i.hasNext() )
				sb.append( "&" );
		}
		return sb.toString();
	}

	/**
	 * XML로 작성된 마지막 요청(void request)을 String 형태로 얻어옵니다.
	 */
	public String getLastResult()
	{
		return this.lastResult;
	}

	/**
	 * 인증 테스트를 수행합니다. 성공할 경우 아무일도 일어나지 않을 것이며,
	 * 인증 파라미터가 잘못되었을 경우 java.io.IOException이 던져질 것입니다.
	 *
	 * @exception IOException 인증정보가 올바르지 않을 때.
	 */
	public void noop() throws IOException
	{
		URL url = new URL("http://me2day.net/api/noop.xml");
		request(url, "GET", null);
	}

	/**
	 * 주어진 미투데이 아이디를 가지는 사용자의 정보를 가져온다.
	 */
	public Person getPerson( String username ) throws IOException
	{
		URL url = new URL(String.format("http://me2day.net/api/get_person/%s.xml",username));
		Document doc = request(url, "GET", null);

		Person ret = null;
		NodeList node = doc.getElementsByTagName("person");
		if( node.getLength()>0 )
			ret = createPersonFromElement((Element)node.item(0));		
		return ret;
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 가져온다.
	 * 
	 * @param username 조회하고자하는 미투데이 아이디
	 */
	public List<Person> getFriends( String username ) throws IOException
	{
		return getFriends(username, "all");
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 그룹별로 가져옵니다.
	 *
	 * @param username 조회하고자하는 미투데이 아이디
	 * @param scope all(모든친구), close(친한친구), family(직계존속), mytag[n](마이태그)
	 */
	public List<Person> getFriends( String username, String scope ) throws IOException
	{
		URL url = new URL(String.format("http://me2day.net/api/get_friends/%s.xml",username));
		Map<String, String> params = new HashMap<String, String>();
		params.put("scope", scope);
		Document doc = request(url, "POST", params);

		List<Person> ret = new ArrayList<Person>();
		NodeList nl = doc.getElementsByTagName("person");
		for(int i=0; i<nl.getLength(); i++)
		{
			Element e = (Element)nl.item(i);
			Person p = createPersonFromElement(e);
			ret.add(p);
		}
		return ret;
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 Me2Id/Person 으로 구성된 Map 형태로 가져온다.
	 */
	public Map<String, Person> getFriendsAsMap( String username ) throws IOException
	{
		URL url = new URL(String.format("http://me2day.net/api/get_friends/%s.xml",username));
		Document doc = request(url, "GET", null);

		Map<String,Person> ret = new HashMap<String,Person>();
		NodeList nl = doc.getElementsByTagName("person");
		for(int i=0; i<nl.getLength(); i++)
		{
			Element e = (Element)nl.item(i);
			Person p = createPersonFromElement(e);
			ret.put( p.getId(), p );
		}
		return ret;
	}

	/**
	 * 주어진 미투데이 사용자가 최근 작성한 글(3개)를 가져온다. 
	 * <p>
	 * Post와 Post가 같은지를 확인하려면, permalink / commentsCount / metooCount 를 
	 * AND 조합으로 확인해야한다. 
	 */
	public List<Post> getLatest( String username ) throws IOException
	{
		URL url = new URL(String.format("http://me2day.net/api/get_latests/%s.xml", username));
		Document doc = request(url, "GET", null);

		List<Post> ret = new ArrayList<Post>();
		NodeList nl = doc.getElementsByTagName("post");
		for(int i=0; i<nl.getLength(); i++)
		{
			Element e = (Element)nl.item(i);
			Post post = new Post();
			post.setPermalink( new URL(getTextAsName(e, "permalink")) );
			post.setBody( getTextAsName(e, "body") );
			post.setKind( getTextAsName(e, "kind") );
			post.setIcon( new URL(getTextAsName(e, "icon")) );
			try
			{
				post.setPubDate( fmtDate.parse(getTextAsName(e, "pubDate")) );
			}
			catch( ParseException ex ) {}
			post.setCommentsCount( 
				Integer.parseInt(getTextAsName(e, "commentsCount")) );
			post.setMetooCount(
				Integer.parseInt(getTextAsName(e, "metooCount")) );
			
			NodeList tags = e.getElementsByTagName("tag");
			for(int j=0; j<tags.getLength(); j++)
			{
				Element eTag = (Element)tags.item(j);
				post.addTag( getTextAsName(eTag, "name") );
			}

			Element author = (Element)e.getElementsByTagName("author").item(0);
			post.setUsername( getTextAsName(author, "id") );

			ret.add(post);
		}

		return ret;
	}

	/**
	 * 주어진 글(퍼머링크)에 달려있는 댓글들을 모두 가져온다.
	 */
	public List<Comment> getComments( String permalink ) throws IOException
	{
		URL url = new URL("http://me2day.net/api/get_comments.xml");
		Map<String, String> params = new HashMap<String, String>();
		params.put("post_id", permalink);

		List<Comment> ret = new ArrayList<Comment>();

		Document doc = request(url, "POST", params);
		NodeList nl = doc.getElementsByTagName("comment");
		for(int i=0; i<nl.getLength(); i++)
		{
			Element e = (Element)nl.item(i);

			Comment comment = new Comment();
			comment.setBody( getTextAsName(e, "body") );
			String date = getTextAsName(e, "pubDate");
			try
			{
				comment.setPubDate( fmtDate.parse(date) );
			}
			catch( ParseException ex ) {}

			Element ea = (Element)e.getElementsByTagName("author").item(0);
			Person author = new Person();
			author.setId( getTextAsName(ea, "id") );
			author.setNickname( getTextAsName(ea, "nickname") );
			/*
			author.setFace( new URL(getTextAsName(ea, "face")) );
			author.setHomepage( new URL(getTextAsName(ea, "homepage")) );
			*/

			comment.setAuthor( author );
			ret.add(comment);
		}
		return ret;
	}

	/**
	 * 지정한 글(퍼머링크)에 주어진 body로 댓글을 작성합니다.
	 */
	public void createComment( String permalink, String body ) throws IOException
	{
		URL url = new URL("http://me2day.net/api/create_comment.xml");
		Map<String, String> params = new HashMap<String, String>();
		params.put("post_id", permalink);
		params.put("body", body);

		Document doc = request(url, "POST", params);
	}

	/**
	 * No document.
	 */
	protected String getTextAsName( Element parent, String childName )
	{
		NodeList nl = parent.getElementsByTagName(childName);
		if( nl.getLength()==0 )
			return null;
		Element child = (Element)nl.item(0);
		Node firstChild = child.getFirstChild();
		if( firstChild!=null )
			return firstChild.getNodeValue();
		return "";
	}

	/**
	 * 주어진 Element (person 태그)의 정보를 기반으로 Person 객체를 생성해준다.
	 */
	protected Person createPersonFromElement( Element e ) throws IOException
	{
		Person ret = new Person();

		ret.setId( getTextAsName(e, "id") );
		ret.setOpenId( new URL(getTextAsName(e, "openid")) );
		ret.setNickname( getTextAsName(e, "nickname") );
		ret.setFace( new URL(getTextAsName(e, "face")) );
		String homepage = getTextAsName(e, "homepage");
		if( homepage!=null && homepage.startsWith("http://") )
			ret.setHomepage( new URL(homepage) );
		ret.setDescription( getTextAsName(e, "description") );
		ret.setRSSDaily( new URL(getTextAsName(e, "rssDaily")) );
		ret.setParentId( getTextAsName(e, "invitedBy") );
		ret.setFriendsCount( Integer.parseInt(getTextAsName(e, "friendsCount")) );
		return ret;
	}
}
