package lotusprize;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;

public class ArtifactToPdf {
    
    static Properties velocityProperties;
    static{
        velocityProperties = new Properties();
        velocityProperties.put("input.encoding", "utf-8");
        velocityProperties.put("output.encoding", "utf-8");
        velocityProperties.put("contentType", "text/html;charset=utf-8");
        velocityProperties.put("file.resource.loader.path","src/main/webapp/WEB-INF/email");
    }

    public static void main(String[] args) throws IOException ,DocumentException{
        renderHtml();
        renderPdf();
    }
    
    private static void renderHtml() throws IOException{
        Velocity.init(velocityProperties);
        VelocityContext context = new VelocityContext();
        context.put("artifact", artifactMap());
        Template template = Velocity.getTemplate("artifact.html");
        Writer  writer =new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("pdf-html/artifact.html")),"UTF-8");
        if (template != null)
            template.merge(context, writer);

        writer.flush();
        writer.close();
    }
    
    private static void renderPdf() throws IOException, DocumentException{
        try(OutputStream os = new FileOutputStream("pdf-html/artifact.pdf")){
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(new File("pdf-html/artifact.html"));
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont("fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        }
    }
    
    private static Map<String,Object> artifactMap(){
        Map<String,Object> artifact= Maps.newHashMap();
        artifact.put("topicName", "OSX界面将全新改版");
        artifact.put("title", "谷歌眼镜五一到货苏宁");
        artifact.put("organization", "苏宁云");
        artifact.put("author", "吉米.雷勒");
        artifact.put("description", "据内部人士确认，谷歌眼镜将于五一期间可以在苏宁多家店面进行体验，具体开卖日期将很快公布。此前，就曾有消息称，苏宁将于今年五一期间，在中国引进谷歌眼镜，海关已经开始有部分眼镜开始陆续进关，苏宁相关的发售店面已经开启了装修，但具体情况还未确认。");
        List<Map<String,Object>> introduces = Lists.newArrayList();
        List<String> images = Lists.newArrayList("file:///D:/hid/bundles/d3d9446802a44259755d38e6d163e820/doc/images/1.jpg","file:///D:/hid/bundles/d3d9446802a44259755d38e6d163e820/doc/images/2.jpg","file:///D:/hid/bundles/d3d9446802a44259755d38e6d163e820/doc/images/3.jpg");
        for(String image:images){
            Map<String,Object> introduce = Maps.newHashMap();
            introduce.put("image", image);
            introduce.put("text", "图片描述,我很长，但是我很好看。Internally, Flying Saucer works with an XML document and uses CSS to determine how to lay it out visually.");
            introduces.add(introduce);
        }
        artifact.put("introduces", introduces);
        return artifact;
    }

}
