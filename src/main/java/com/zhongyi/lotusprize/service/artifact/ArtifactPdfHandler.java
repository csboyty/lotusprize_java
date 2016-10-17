package com.zhongyi.lotusprize.service.artifact;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.service.bcs.LotusprizeBcsFiles;
import com.zhongyi.lotusprize.web.misc.LotusprizeVelocityTool;

@Component
public class ArtifactPdfHandler extends ArtifactRetriveHandler{
    
    private static final LotusprizeVelocityTool lotusprizeTool = new LotusprizeVelocityTool();
    
    @Autowired
    @Qualifier("emailVelocityEngine")
    private VelocityEngine velocityEngine;
    
    @Autowired
    private AccountRedis accountRedis;
    
    private String chineseFontPath;
    
    @PostConstruct
    public void init(){
        chineseFontPath = ApplicationProperty.instance().servletContext().getRealPath("fonts/STZHONGS.TTF");
    }

    
    public Map<String,Object> exportAsPdf(Integer artifactId,boolean showAuthorInfo,String lang) throws IOException, DocumentException{
        Map<String,Object> artifactMap = artifactById(artifactId,lang);
        Account account = accountRedis.accountById((Integer)artifactMap.get("ownAccountId"));
        String version = "v"+(showAuthorInfo?"1":"0");
        String pdfFilename = artifactId+"/"+version+"/"+artifactId+"_"+lang+".pdf";
        String pdfFilePath = LotusprizeLocalFiles.instance().pdfFilePath(pdfFilename);
        File pdfFile = new File(pdfFilePath);
        if(!pdfFile.exists()){
            File parentDir = new File(LotusprizeLocalFiles.instance().pdfLocalStorageDir(),artifactId+"/"+version);
            if(!parentDir.exists())parentDir.mkdirs();
            List<File> htmlFiles = renderHtml(artifactMap,account,showAuthorInfo,parentDir);
            renderPdf(htmlFiles,pdfFile);
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("filename", artifactMap.get("title")+".pdf");
        result.put("file", pdfFile);
        return result;
    }
    
    private void toLocalFilePath(Map<String,Object> introduce){
        String image = (String)introduce.get("image");
        String imageFilePath;
        if(LotusprizeBcsFiles.instance().isBcsFile(image)){
            String userPartPath = LotusprizeBcsFiles.instance().bcsObjectName(image);
            String localFile = LotusprizeLocalFiles.instance().localFilePath(userPartPath);
            imageFilePath = "file:///" + localFile;
        }else{
            imageFilePath = "file:///" + image;
        }
        introduce.put("image", imageFilePath);
    }
    
    private List<File> renderHtml(Map<String,Object>artifactMap,Account account,boolean showAuthorInfo,File parentDir) throws IOException{
        List<File> htmlFiles = Lists.newArrayList();
        Map<String,Object> context = Maps.newHashMap();
        context.put("artifact", artifactMap);
        context.put("author",account);
        context.put("showAuthorInfo",showAuthorInfo);
        context.put("lotusprizeTool", lotusprizeTool);
        int i =0;
        File infoHtmlFile = new File(parentDir,String.valueOf(i)+".html");
        Writer infoWriter =new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(infoHtmlFile)),"UTF-8");
        VelocityEngineUtils.mergeTemplate(velocityEngine, "artifact_info_pdf.html", ApplicationProperty.instance().encoding(), context, infoWriter);
        infoWriter.flush();
        infoWriter.close();
        htmlFiles.add(infoHtmlFile);
        i++;
        context.clear();
        for(Map<String,Object> introduce:(List<Map<String,Object>>)artifactMap.get("introduces")){
            toLocalFilePath(introduce);
            context.put("introduce", introduce);
            context.put("lotusprizeTool", lotusprizeTool);
            File introdHtmlFile = new File(parentDir,String.valueOf(i)+".html");
            Writer introdWriter =new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(introdHtmlFile)),"UTF-8");
            VelocityEngineUtils.mergeTemplate(velocityEngine, "artifact_introduce_pdf.html", ApplicationProperty.instance().encoding(), context, introdWriter);
            introdWriter.flush();
            introdWriter.close();
            htmlFiles.add(introdHtmlFile);
            i++;
            context.clear();
        }
        return htmlFiles;
    }
    
    private void renderPdf(List<File> htmlFiles,File pdfFile) throws IOException, DocumentException{
        try(OutputStream pdfOutput = new FileOutputStream(pdfFile)){
            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(chineseFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            Iterator<File> htmlFileIt = htmlFiles.iterator();
            File firstHtmlFile = htmlFileIt.next();
            renderer.setDocument(firstHtmlFile);
            renderer.layout();    
            renderer.createPDF(pdfOutput, false);   
            while(htmlFileIt.hasNext()){
                renderer.setDocument(htmlFileIt.next());
                renderer.layout();
                renderer.writeNextDocument();
            }
            renderer.finishPDF();
        }catch(IOException ex){
            pdfFile.delete();
        }
        
    }
}
