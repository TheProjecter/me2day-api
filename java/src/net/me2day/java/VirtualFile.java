package net.me2day.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.me2day.java.event.ProgressListener;

/**
 * 
 * @author Jang-Ho Hwang, rath@xrath.com 
 *
 */
public class VirtualFile {
	
	private File file;
	private InputStream stream;
	
	private String name;
	private long length;
	private ProgressListener listener;
	
	private VirtualFile() {
		
	}
	
	public static VirtualFile create( File f ) {
		VirtualFile v = new VirtualFile();
		v.setFile(f);
		v.setName( f.getName() );
		v.setLength( f.length() );
		return v;
	}
	
	public static VirtualFile create( String name, InputStream in, long length ) {
		VirtualFile v = new VirtualFile();
		v.setStream(in);
		v.setName(name);
		v.setLength(length);
		return v;
	}

	private void setFile(File file) {
		this.file = file;
	}

	private void setStream(InputStream stream) {
		this.stream = stream;
	}

	public InputStream getInputStream() throws IOException {
		if( stream!=null )
			return stream;
		return new FileInputStream(this.file);		
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void setLength(long length) {
		this.length = length;
	}

	public long getLength() {
		return length;
	}
	
	/**
	 * 이 첨부파일을 전송할 경우 진행상황을 보고받을 이벤트 리스너를 가져온다.
	 */
	public ProgressListener getProgressListener()
	{
		return this.listener;
	}
	
	/**
	 * 이 첨부파일을 전송할 경우 진행상황을 보고받을 이벤트 리스너를 지정한다.
	 */
	public void setProgressListener( ProgressListener l )
	{
		this.listener = l;
	}
}
