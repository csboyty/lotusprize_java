package lotusprize;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.velocity.tools.generic.EscapeTool;

import com.google.common.html.HtmlEscapers;
import com.google.common.xml.XmlEscapers;

public class VelocityToolTest {
    
    public static void main(String[]args) throws UnsupportedEncodingException{
        
        String s = "\"See & Touch\"-Haptic <Display> Museum Guide 藝術品觸覺體驗裝置";
        System.out.println(new EscapeTool().html("http://www.lotusprize.com/lp/upload-file/5717/%22GoldenSection%22创意冰箱设计(53ba607b0cf2df46f46027fb).zip"));
        System.out.println(HtmlEscapers.htmlEscaper().escape(s));
        s= "See&Touch(53b17bc20cf2c1bacc3e9d28).zip";
        System.out.println(URLEncoder.encode(s, "utf-8"));
        s = "在危險的高速公路上架設";
        System.out.println(XmlEscapers.xmlContentEscaper().escape(s));
        
        
        
    }

}
