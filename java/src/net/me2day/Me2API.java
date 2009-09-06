package net.me2day;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.me2day.entity.Comment;
import net.me2day.entity.Icon;
import net.me2day.entity.Metoo;
import net.me2day.entity.Person;
import net.me2day.entity.Post;
import net.me2day.entity.TrackComment;
import net.me2day.event.ProgressEvent;
import net.me2day.event.ProgressListener;
import net.me2day.util.BASE64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 미투데이(http://me2day.net)에 글을 포스트할 수 있게 간단히 wrapping 한 클래스입니다.
 * <p>
 * 자세한 사항은 <a href="http://codian.springnote.com/pages/86001">codian님의
 * 스프링노트</a>를 보시기 바랍니다.
 * <p>
 * 간단한 사용 예는 다음과 같습니다.
 * 
 * <pre>
 * &lt;font face=&quot;돋움체&quot; color=&quot;#336699&quot;&gt;
 * Me2API me2 = new Me2API();
 * me2.setUsername(&quot;rath&quot;);
 * me2.setUserKey(&quot;00112233&quot;); 
 * me2.setApplicationKey(&quot;000000000000&quot;); 
 * me2.post(&quot;아무리 할 일이 많아도 여유를 잃은 삶 따위는 살지 않으리.&quot;, &quot;*활짝*&quot;, ICON_THINK);
 * &lt;/font&gt;
 * </pre>
 * 
 * @author Jang-Ho Hwang, rath@xrath.com
 */
public class Me2API {
	private static final SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private Random rng = new Random(System.currentTimeMillis());

	private String username;
	private String userKey;
	private String appKey;

	private DocumentBuilder docBuilder;
	private String lastResult = null;

	public Me2API() throws ParserConfigurationException {
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	/**
	 * 미투데이 사용자 이름을 지정합니다.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 미투데이 사용자 이름을 가져옵니다.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 00112233 형식의 미투데이 <b>사용자 키</b>를 지정합니다. 미투데이 관리
	 * 페이지(http://me2day.net/yourname/setting)에서 가져올 수 있습니다. *
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	/**
	 * 미투데이 사용자 키를 가져옵니다.
	 */
	public String getUserKey() {
		return this.userKey;
	}

	/**
	 * me2API 어플리케이션 키를 설정합니다. <a
	 * href="http://me2day.net/api/front/appkey">http:
	 * //me2day.net/api/front/appkey</a>에서 신청하면 메일로 받을 수 있습니다.
	 */
	public void setApplicationKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * 설정해둔 me2API 어플리케이션 키를 가져옵니다.
	 */
	public String getApplicationKey() {
		return this.appKey;
	}

	private String getHexaDecimal(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int v = (int) b[i];
			if (v < 0)
				v += 0x100;
			String s = Integer.toHexString(v);
			if (s.length() == 1)
				sb.append('0');
			sb.append(s);
		}
		return sb.toString();
	}

	private String generateNonce() {
		byte[] buf = new byte[4];
		rng.nextBytes(buf);
		return getHexaDecimal(buf);
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 이 메서드를 사용할 경우 태그는 붙지 않고 분류 아이콘은 '생각'으로 지정됩니다.
	 * 
	 * @param msg
	 *            - 남기고자 하는 글.
	 */
	public String post(String msg) throws IOException {
		return post(msg, "", Post.ICON_THINK);
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 이 메서드를 사용할 경우 태그는 붙지 않습니다.
	 * 
	 * @param msg
	 *            - 남기고자 하는 글.
	 * @param icon
	 *            - ICON_THINK, ICON_FEELING, ICON_NOTICE 중에 하나.
	 */
	public String post(String msg, int icon) throws IOException {
		return post(msg, "", icon);
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 이 메서드를 사용할 경우 분류 아이콘은 '생각'이 됩니다.
	 * 
	 * @param msg
	 *            - 남기고자 하는 글.
	 * @param tags
	 *            - 태그. 태그가 여러개일 경우 공백으로 구분해 넣어야 함.
	 */
	public String post(String msg, String tags) throws IOException {
		return post(msg, tags, Post.ICON_THINK);
	}

	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 이 메서드를 사용할 경우 분류 아이콘은 '생각'이 됩니다.
	 * 
	 * @param msg
	 *            - 남기고자 하는 글.
	 * @param tags
	 *            - 태그. 태그가 여러개일 경우 공백으로 구분해 넣어야 함.
	 * @param icon
	 *            - ICON_THINK, ICON_FEELING, ICON_NOTICE 중에 하나.
	 */
	public String post(String msg, String tags, int icon) throws IOException {
		Post post = new Post();
		post.setBody(msg);
		post.setTags(tags);
		post.setIconIndex(icon);
		
		return post(post);
	}
	
	/**
	 * 자신의 미투데이에 새로운 글을 포스트합니다. 이 메서드를 사용할 경우 분류 아이콘은 '생각'이 됩니다.
	 *
	 */
	public String post(Post post) throws IOException {
		if (post.getLength() > 150)
			throw new IllegalArgumentException("message.length must less than 150");
		if (post.getIconIndex() < 1 || post.getIconIndex() > 12)
			throw new IllegalArgumentException("icon must be between 1 and 12");
		if (username == null)
			throw new IllegalStateException("username cannot be null");
		if (userKey == null)
			throw new IllegalStateException("userKey cannot be null");
		if (appKey == null)
			throw new IllegalStateException("appKey cannot be null");

		URL url = new URL("http://me2day.net/api/create_post");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("post[body]", post.getBody());
		params.put("post[tags]", post.getTags());
		params.put("post[icon]", String.valueOf(post.getIconIndex()));
		params.put("receive_sms", String.valueOf(post.isReceiveSMS()));
		params.put("close_comment", String.valueOf(post.isCloseComment()));

		if (post.hasLocation()) {
			params.put("longitude", String.valueOf(post.getLongitude()));
			params.put("latitude", String.valueOf(post.getLatitude()));
		}

		Document doc = null;
		if (post.getAttachment() != null ) {
			params.put("attachment", post.getAttachment());
			doc = requestMultipart(url, params);
		} else {
			doc = request(url, "POST", params);
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String permalink = null;
		try {
			permalink = xpath.evaluate("/post/permalink/text()", doc);
		} catch (XPathExpressionException e) {
		}
		return permalink;
	}

	protected Document requestMultipart(URL url, Map<String, Object> params) throws IOException {
		String delimeter = "---------------------------7d115d2a20060c";
		byte[] newLineBytes = "\r\n".getBytes();
		byte[] delimeterBytes = delimeter.getBytes();
		byte[] dispositionBytes = "Content-Disposition: form-data; name=".getBytes();
		byte[] quotationBytes = "\"".getBytes();
		byte[] contentTypeBytes = "Content-Type: application/octet-stream".getBytes();
		byte[] fileNameBytes = "; filename=".getBytes();
		byte[] twoDashBytes = "--".getBytes();

		String encodeKey = new BASE64(false).encode(username + ":" + createPassword());
		String authKey = "Basic " + encodeKey;

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + delimeter);
		con.setRequestProperty("Authorization", authKey);
		con.setRequestProperty("me2_application_key", this.appKey);

		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);

		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(con.getOutputStream());

			Iterator<String> iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();

				out.write(twoDashBytes);
				out.write(delimeterBytes);
				out.write(newLineBytes);

				out.write(dispositionBytes);
				out.write(quotationBytes);
				out.write(key.getBytes());
				out.write(quotationBytes);

				Object value = params.get(key);
				if (value instanceof String) {
					out.write(newLineBytes);
					out.write(newLineBytes);
					out.write(String.valueOf(value).getBytes());
					out.write(newLineBytes);
				} else if (value instanceof VirtualFile) {
					VirtualFile file = (VirtualFile)value;
					InputStream in = null;
					try {
						out.write(fileNameBytes);
						out.write(quotationBytes);
						out.write(file.getName().getBytes());
						out.write(quotationBytes);
						out.write(newLineBytes);
						out.write(contentTypeBytes);
						out.write(newLineBytes);
						out.write(newLineBytes);
						byte[] fileBuffer = new byte[1024 * 8]; // 8k
						
						long total = file.getLength();
						in = file.getInputStream();
						ProgressListener l = file.getProgressListener();
						
						int len = -1;
						while ((len = in.read(fileBuffer)) != -1) {
							out.write(fileBuffer, 0, len);
							if (l != null)
								l.transferProgress(new ProgressEvent(this, len, total));
						}
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (IOException ex) {}
					}
					out.write(newLineBytes);
				}
			}

			out.write(twoDashBytes);
			out.write(delimeterBytes);
			out.write(twoDashBytes);
			out.write(newLineBytes);
			out.flush();
		} finally {
			if (out != null)
				out.close();
		}

		return readResult(con);
	}

	private Document readResult(HttpURLConnection con) throws IOException {
		Document ret = null;
		InputStream in = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		try {
			in = con.getInputStream();
			while (true) {
				int readlen = in.read(buf);
				if (readlen < 1)
					break;
				bos.write(buf, 0, readlen);
			}
			String output = new String(bos.toByteArray(), "UTF-8");
			System.out.println(output);

			this.lastResult = output;
			try {
				ret = docBuilder.parse(new InputSource(new StringReader(output)));
			} catch (SAXException ex) {
				this.lastResult = ex.toString();
			}
		} catch (IOException e) {
			if (con.getResponseCode() == 500) {
				bos.reset();
				InputStream err = con.getErrorStream();
				while (true) {
					int readlen = err.read(buf);
					if (readlen < 1)
						break;
					bos.write(buf, 0, readlen);
				}
				String output = new String(bos.toByteArray(), "UTF-8");
				System.err.println(output);
			}
			throw e;
		} finally {
			if (in != null)
				in.close();
			if (con != null)
				con.disconnect();
		}
		return ret;
	}

	private String createPassword() {
		String nonce = generateNonce();
		String digest = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(nonce.getBytes());
			md.update(this.userKey.getBytes());
			digest = getHexaDecimal(md.digest());
		} catch (Exception e) {
			System.err.println("Warning: No provider for MD5 Digest exists!");
		}

		return nonce + digest;
	}

	/**
	 * INTERNAL USE ONLY
	 */
	protected Document request(URL url, String method, Map<String, Object> params) throws IOException {
		Document ret = null;

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);

		if (this.userKey != null) {
			String authKey = String.format("Basic %s", new BASE64(false).encode(username + ":" + createPassword()));
			con.setRequestProperty("Authorization", authKey);
		}

		if (this.appKey != null)
			con.setRequestProperty("me2_application_key", this.appKey);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setDoInput(true);

		if (method.equals("POST")) {
			con.setDoOutput(true);
			String paramstr = buildParameters(params);
			OutputStream out = con.getOutputStream();
			out.write(paramstr.getBytes("UTF-8"));
			out.flush();
			out.close();
		}

		InputStream in = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		try {
			in = con.getInputStream();
			while (true) {
				int readlen = in.read(buf);
				if (readlen < 1)
					break;
				bos.write(buf, 0, readlen);
			}
			String output = new String(bos.toByteArray(), "UTF-8");
			// System.out.println(output);

			this.lastResult = output;
			try {
				ret = docBuilder.parse(new InputSource(new StringReader(output)));
			} catch (SAXException ex) {
				this.lastResult = ex.toString();
			}
		} catch (IOException e) {
			if (con.getResponseCode() == 500) {
				bos.reset();
				InputStream err = con.getErrorStream();
				while (true) {
					int readlen = err.read(buf);
					if (readlen < 1)
						break;
					bos.write(buf, 0, readlen);
				}
				String output = new String(bos.toByteArray(), "UTF-8");
				System.err.println(output);
			}
			throw e;
		} finally {
			if (in != null)
				in.close();
			if (con != null)
				con.disconnect();
		}
		return ret;
	}

	/**
	 * 주어진 Map의 key/value를 application/x-www-form-urlencoded 형식으로 만들어줍니다.
	 */
	protected String buildParameters(Map<String, Object> params) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			sb.append(key);
			sb.append("=");
			sb.append(URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8"));
			if (i.hasNext())
				sb.append("&");
		}
		return sb.toString();
	}

	/**
	 * XML로 작성된 마지막 요청(void request)을 String 형태로 얻어옵니다.
	 */
	public String getLastResult() {
		return this.lastResult;
	}

	/**
	 * 인증 테스트를 수행합니다. 성공할 경우 아무일도 일어나지 않을 것이며, 인증 파라미터가 잘못되었을 경우
	 * java.io.IOException이 던져질 것입니다.
	 * 
	 * @exception IOException
	 *                인증정보가 올바르지 않을 때.
	 */
	public void noop() throws IOException {
		URL url = new URL("http://me2day.net/api/noop.xml");
		request(url, "GET", null);
	}

	/**
	 * 주어진 미투데이 아이디를 가지는 사용자의 정보를 가져온다.
	 */
	public Person getPerson(String username) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/get_person/%s.xml", username));
		Document doc = request(url, "GET", null);

		Person ret = null;
		NodeList node = doc.getElementsByTagName("person");
		if (node.getLength() > 0)
			ret = createPersonFromElement((Element) node.item(0));
		return ret;
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 가져온다.
	 * 
	 * @param username
	 *            조회하고자하는 미투데이 아이디
	 */
	public List<Person> getFriends(String username) throws IOException {
		return getFriends(username, "all");
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 그룹별로 가져옵니다.
	 * 
	 * @param username
	 *            조회하고자하는 미투데이 아이디
	 * @param scope
	 *            all(모든친구), close(친한친구), family(직계존속), mytag[n](마이태그)
	 */
	public List<Person> getFriends(String username, String scope) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/get_friends/%s.xml", username));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scope", scope);
		Document doc = request(url, "POST", params);

		List<Person> ret = new ArrayList<Person>();
		NodeList nl = doc.getElementsByTagName("person");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Person p = createPersonFromElement(e);
			ret.add(p);
		}
		return ret;
	}

	/**
	 * 주어진 미투데이 사용자의 친구 목록을 Me2Id/Person 으로 구성된 Map 형태로 가져온다.
	 */
	public Map<String, Person> getFriendsAsMap(String username) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/get_friends/%s.xml", username));
		Document doc = request(url, "GET", null);

		Map<String, Person> ret = new HashMap<String, Person>();
		NodeList nl = doc.getElementsByTagName("person");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Person p = createPersonFromElement(e);
			ret.put(p.getId(), p);
		}
		return ret;
	}

	/**
	 * 주어진 commentId를 가지는 댓글을 삭제합니다. 사용자 인증이 필요합니다.
	 * 
	 * @param commentId
	 *            get_comments 에서 반환된 댓글을 지정하는 아이디 입니다.
	 */
	public void deleteComment(String commentId) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/delete_comment.xml?comment_id=%s", commentId));

		request(url, "GET", null);
	}

	/**
	 * 주어진 미투데이 사용자가 최근 작성한 글(3개)를 가져온다.
	 * <p>
	 * Post와 Post가 같은지를 확인하려면, permalink / commentsCount / metooCount 를 AND 조합으로
	 * 확인해야한다.
	 */
	public List<Post> getLatest(String username) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/get_latests/%s.xml", username));
		Document doc = request(url, "GET", null);

		List<Post> ret = new ArrayList<Post>();
		NodeList nl = doc.getElementsByTagName("post");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Post post = new Post();
			post.setId(getTextAsName(e, "post_id"));
			post.setPermalink(new URL(getTextAsName(e, "permalink")));
			post.setBody(getTextAsName(e, "body"));
			post.setKind(getTextAsName(e, "kind"));
			post.setIcon(new URL(getTextAsName(e, "icon")));
			try {
				post.setPubDate(fmtDate.parse(getTextAsName(e, "pubDate")));
			} catch (ParseException ex) {
			}
			post.setCommentsCount(Integer.parseInt(getTextAsName(e, "commentsCount")));
			post.setMetooCount(Integer.parseInt(getTextAsName(e, "metooCount")));

			NodeList tags = e.getElementsByTagName("tag");
			for (int j = 0; j < tags.getLength(); j++) {
				Element eTag = (Element) tags.item(j);
				post.addTag(getTextAsName(eTag, "name"));
			}

			Element author = (Element) e.getElementsByTagName("author").item(0);
			post.setUsername(getTextAsName(author, "id"));

			ret.add(post);
		}

		return ret;
	}

	/**
	 * 주어진 post에 metoo 한 사람들의 목록을 가져온다.
	 * 
	 * @param postId
	 *            post_id.
	 */
	public List<Metoo> getMetoos(String postId) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/get_metoos.xml?post_id=%s", postId));

		Document doc = request(url, "GET", null);

		List<Metoo> ret = new ArrayList<Metoo>();
		NodeList nl = doc.getElementsByTagName("metoo");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			Metoo m = new Metoo();
			String date = getTextAsName(e, "pubDate");
			try {
				m.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			Person author = new Person();
			author.setId(getTextAsName(e, "id"));
			author.setNickname(getTextAsName(e, "nickname"));
			author.setFace(new URL(getTextAsName(e, "face")));
			m.setAuthor(author);

			ret.add(m);
		}

		return ret;
	}

	/**
	 * 주어진 post_id와 관련된 포스트에 metoo를 한다. 상세한 정보는 <a
	 * href="http://codian.springnote.com/pages/404723">track_comments</a> 문서를
	 * 참조하라.
	 * 
	 * @param post_id
	 *            post_id.
	 */
	public void metoo(String post_id) throws IOException {
		URL url = new URL("http://me2day.net/api/metoo.xml");
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("post_id", post_id);

		@SuppressWarnings("unused")
		Document doc = request(url, "POST", param);
	}

	/**
	 * 주어진 userid가 최근에 작성한 댓글을 count 만큼 가져온다. 상세한 정보는 <a
	 * href="http://codian.springnote.com/pages/404723">track_comments</a> 문서를
	 * 참조하라.
	 * 
	 * @param userid
	 *            미투데이 아이디
	 * @param count
	 *            가져올 댓글 개수
	 */
	public List<TrackComment> getCommentsByMe(String userid, int count) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/track_comments/%s.xml", userid));
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("scope", "by_me");
		param.put("count", String.valueOf(count));

		Document doc = request(url, "POST", param);

		List<TrackComment> ret = new ArrayList<TrackComment>(count);
		NodeList nl = doc.getElementsByTagName("commentByMe");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			Element ep = (Element) e.getElementsByTagName("post").item(0);
			Element ec = (Element) e.getElementsByTagName("comment").item(0);

			String date = null;

			Post post = new Post();
			post.setId(getTextAsName(ep, "post_id"));
			post.setPermalink(new URL(getTextAsName(ep, "permalink")));
			post.setBody(getTextAsName(ep, "body"));
			date = getTextAsName(ep, "pubDate");
			try {
				post.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			Comment comment = new Comment();
			comment.setBody(getTextAsName(ec, "body"));
			date = getTextAsName(ec, "pubDate");
			try {
				comment.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			Person author = new Person();
			author.setId(getTextAsName(ec, "id"));
			author.setNickname(getTextAsName(ec, "nickname"));
			comment.setAuthor(author);

			TrackComment tc = new TrackComment();
			tc.setPost(post);
			tc.setComment(comment);

			ret.add(tc);
		}
		return ret;
	}

	/**
	 * 주어진 userid가 최근에 받은 댓글을 count 만큼 가져온다.
	 * 
	 * @param userid
	 *            미투데이 아이디
	 * @param count
	 *            가져올 댓글 개수
	 */
	public List<TrackComment> getCommentsToMe(String userid, int count) throws IOException {
		URL url = new URL(String.format("http://me2day.net/api/track_comments/%s.xml", userid));
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("scope", "to_me");
		param.put("count", String.valueOf(count));

		Document doc = request(url, "POST", param);

		List<TrackComment> ret = new ArrayList<TrackComment>(count);
		NodeList nl = doc.getElementsByTagName("commentToMe");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			Element ep = (Element) e.getElementsByTagName("post").item(0);
			Element ec = (Element) e.getElementsByTagName("comment").item(0);

			String date = null;

			Post post = new Post();
			post.setId(getTextAsName(ep, "post_id"));
			post.setPermalink(new URL(getTextAsName(ep, "permalink")));
			post.setBody(getTextAsName(ep, "body"));
			date = getTextAsName(ep, "pubDate");
			try {
				post.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			Comment comment = new Comment();
			comment.setBody(getTextAsName(ec, "body"));
			date = getTextAsName(ec, "pubDate");
			try {
				comment.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			TrackComment tc = new TrackComment();
			tc.setPost(post);
			tc.setComment(comment);

			ret.add(tc);
		}
		return ret;
	}

	/**
	 * 주어진 글(퍼머링크)에 달려있는 댓글들을 모두 가져온다.
	 */
	public List<Comment> getComments(String permalink) throws IOException {
		URL url = new URL("http://me2day.net/api/get_comments.xml");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("post_id", permalink);

		List<Comment> ret = new ArrayList<Comment>();

		Document doc = request(url, "POST", params);
		NodeList nl = doc.getElementsByTagName("comment");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);

			Comment comment = new Comment();
			comment.setId(getTextAsName(e, "commentId"));
			comment.setBody(getTextAsName(e, "body"));
			String date = getTextAsName(e, "pubDate");
			try {
				comment.setPubDate(fmtDate.parse(date));
			} catch (ParseException ex) {
			}

			Element ea = (Element) e.getElementsByTagName("author").item(0);
			Person author = new Person();
			author.setId(getTextAsName(ea, "id"));
			author.setNickname(getTextAsName(ea, "nickname"));
			/*
			 * author.setFace( new URL(getTextAsName(ea, "face")) );
			 * author.setHomepage( new URL(getTextAsName(ea, "homepage")) );
			 */

			comment.setAuthor(author);
			ret.add(comment);
		}
		return ret;
	}

	/**
	 * 지정한 글(퍼머링크)에 주어진 body로 댓글을 작성합니다.
	 */
	public void createComment(String permalink, String body) throws IOException {
		URL url = new URL("http://me2day.net/api/create_comment.xml");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("post_id", permalink);
		params.put("body", body);

		request(url, "POST", params);
	}

	/**
	 * No document.
	 */
	protected String getTextAsName(Element parent, String childName) {
		NodeList nl = parent.getElementsByTagName(childName);
		if (nl.getLength() == 0)
			return null;
		Element child = (Element) nl.item(0);
		Node firstChild = child.getFirstChild();
		if (firstChild != null)
			return firstChild.getNodeValue();
		return "";
	}

	/**
	 * 주어진 Element (person 태그)의 정보를 기반으로 Person 객체를 생성해준다.
	 */
	protected Person createPersonFromElement(Element e) throws IOException {
		Person ret = new Person();

		ret.setId(getTextAsName(e, "id"));
		String openId = getTextAsName(e, "openid");
		if( openId!=null && openId.startsWith("http") )
			ret.setOpenId(new URL(openId));
		ret.setNickname(getTextAsName(e, "nickname"));
		ret.setFace(new URL(getTextAsName(e, "face")));
		String homepage = getTextAsName(e, "homepage");
		if (homepage != null && homepage.startsWith("http://"))
			ret.setHomepage(new URL(homepage));
		ret.setDescription(getTextAsName(e, "description"));
		ret.setRSSDaily(new URL(getTextAsName(e, "rssDaily")));
		ret.setParentId(getTextAsName(e, "invitedBy"));
		ret.setFriendsCount(Integer.parseInt(getTextAsName(e, "friendsCount")));
		
		// postIcons
		NodeList iconList = e.getElementsByTagName("postIcon");
		List<Icon> icons = new ArrayList<Icon>();
		for(int i=0; i<iconList.getLength(); i++) {
			Element iconElement = (Element)iconList.item(i);			
			Icon icon = new Icon();
			icon.setIndex(Integer.parseInt(getTextAsName(iconElement, "iconIndex")));
			try {
				icon.setType(Integer.parseInt(getTextAsName(iconElement, "iconType")));
			} catch( NumberFormatException ne ) {
				icon.setType(1);
			}
			icon.setDescription(getTextAsName(iconElement, "description"));
			icon.setURL(new URL(getTextAsName(iconElement, "url")));
			icon.setDefault(Boolean.getBoolean(getTextAsName(iconElement, "default")));
			icons.add(icon);
		}
		ret.setPostIcons(icons);
		
		return ret;
	}
}
