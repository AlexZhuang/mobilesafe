package com.alex.mobilesafe.engine;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alex.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {
	
	/**
	 * 使用XmlPullParser解析，并返回UpdateInfo 对象
	 * @param is XML文件输入流
	 * @return UpdateInfo 对象
	 * @throws Exception
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{
		UpdateInfo info = new UpdateInfo();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, "utf-8");
        int eventType = xpp.getEventType();
        while(eventType!=XmlPullParser.END_DOCUMENT){
        	if (eventType==XmlPullParser.START_TAG) {
				if("version".equals(xpp.getName())){
					info.setVersion(xpp.nextText());
				}else if("description".equals(xpp.getName())){
					info.setDescription(xpp.nextText());
				}else if("apkurl".equals(xpp.getName())){
					info.setApkurl(xpp.nextText());
				}
			}
        	eventType=xpp.next();
        }
        
		return info;
	}
}
