/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-3-11
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
var uploadWork=(function(config){


    var maxMediaLength=3;
    var uploadedMedias={

    };
    var mediaUploader=null;
    var currentMediaKey="";
    return {
        createThumbUploader:function(){
            config.createUploader({
                size:config.uploadSize.img,
                filter:config.uploadFilter.img,
                btn:"uploadThumb",
                multipartParams:{
                    isThumb:true
                },
                container:"uploadThumbContainer",
                url:config.ajaxUrls.uploadFile,
                callback:function(res,filename){
                    var response=JSON.parse(res.response);
                    if(response.success){
                        $("#workThumb").val(response.uri);
                        $("#workThumbShow").attr("src",response.uri);
                    }else{
                        config.ajaxReturnErrorHandler(response);
                    }
                }
            });
        },
        createAttaUploader:function(){
            config.createUploader({
                size:config.uploadSize.attachment,
                filter:config.uploadFilter.attachment,
                btn:"uploadAttachment",
                container:"uploadAttachmentContainer",
                url:config.ajaxUrls.uploadFile,
                progressCallback:function(filePercent){
                    if(filePercent==100){
                        $("#attachmentRequire").html(config.message.progress);
                    }else{
                        $("#attachmentRequire").html(filePercent+"%");
                    }
                },
                callback:function(res,filename){
                    var response=JSON.parse(res.response);
                    if(response.success){
                        var tpl=$("#attaTpl").html();
                        var html=juicer(tpl,{
                            filename:filename,
                            filepath:response.uri
                        });
                        $("#attachmentRow .attachment").remove();
                        $("#attachmentRow").append(html);
                        $("#attachmentRequire").html(config.message.uploaded);
                    }else{
                        config.ajaxReturnErrorHandler(response);
                    }
                }
            });
        },
        createMediaUploader:function(){
            var me=this;
            mediaUploader=config.createUploader({
                size:config.uploadSize.img,
                filter:config.uploadFilter.img,
                btn:"uploadMedia",
                multipartParams:{
                    isWorkImg:true
                },
                container:"uploadMediaContainer",
                url:config.ajaxUrls.uploadFile,
                fileAddCallback:function(up,files){

                    //判断是否超过了三个
                    if($("#mediaList li").length<maxMediaLength){
                        if(files[0].name.length>config.maxFileNameLength){

                            $().toastmessage("showErrorToast",config.message.fileNameTooLong.replace("${value}",config.maxFileNameLength));

                            //删除文件
                            up.removeFile(files[0]);
                        }else{
                            up.start();
                        }
                    }else{
                        $().toastmessage("showErrorToast",config.message.workMediaTooMuch.replace("${value}",maxMediaLength));

                        //删除文件
                        up.removeFile(files[0]);
                    }
                },
                callback:function(res,filename){

                    var response=JSON.parse(res.response);
                    if(response.success){
                        var key=new Date().getTime();
                        me.showMedia(filename,key);
                        uploadedMedias[key]={
                            image:response.uri,
                            text:""
                        };
                        if($("#mediaList li").length==1){
                            me.setMediaFirstActive();
                        }
                    }else{
                        config.ajaxReturnErrorHandler(response);
                    }

                }
            });
        },
        showMedia:function(filename,key){
            var tpl=$("#mediaTpl").html();
            var html=juicer(tpl,{
                filename:filename,
                key:key
            });
            $("#mediaList").append(html);
        },
        submitHandler:function(el){

            config.showBlackout();

            var mediaObj={};

            $("#mediaList li").each(function(index,el){
                var key=$(el).find(".file").attr("href");
                mediaObj[index]={
                    image:uploadedMedias[key]["image"],
                    text:uploadedMedias[key]["text"]
                }
            });

            el.ajaxSubmit({
                data:{
                    introduce:JSON.stringify(mediaObj)
                },
                dataType:"json",
                success:function(data){
                    if(data.success){
                        $().toastmessage("showSuccessToast",config.message.optSuccRedirect);
                        if(location.href.match("_lang=en")!=null){
                            config.redirect("s/user/artifact?_lang=en");
                        }else{
                            config.redirect("s/user/artifact");
                        }

                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });


        },
        preview:function(){
            var tpl=$("#previewTpl").html();
            var medias=[];
            $("#mediaList li").each(function(index,el){
                var key=$(el).find(".file").attr("href");
                medias.push(uploadedMedias[key]);
            });

            var data={
                workTitle:$("#workTitle").val(),
                medias:medias,
                workTopic:$("#workTopic").text(),
                workThumb:$("#workThumb").val(),
                workAuthor:$("#workAuthor").val(),
                workOrganization:$("#workOrganization").val(),
                workDescription:$("#workDescription").val()
            };
            var html=juicer(tpl,data);
            $("#step3").html(html);
        },
        drag:function(){
            var targetOl = document.getElementById("mediaList");//容器元素
            var eleDrag = null;//被拖动的元素

            document.onselectstart=function(event){
                if(event.target.className=="name"){
                    return false;
                }
            };
            document.ondragstart=function(event){
                if(event.target.className=="name"){
                    event.dataTransfer.effectAllowed = "move";
                    eleDrag = event.target;
                }
            };
            document.ondragend=function(event){
                if(event.target.className=="name"){
                    eleDrag=null;
                    return false;
                }
            };

            //在元素中滑过
            targetOl.ondragover = function (ev) {
                ev.preventDefault();//阻止浏览器的默认事件
                return false;
            };

            //ol作为最大的容器也要处理拖拽事件，当在li上滑动的时候放到li的前面，当在ol上滑动的时候放到ol的最后面
            targetOl.ondragenter = function (ev) {
                var eleDragParent=eleDrag.parentNode.parentNode;
                var target=ev.toElement;
                var targetParent=target.parentNode;
                var targetParents=target.parentNode.parentNode;
                if (target == targetOl) {
                    targetOl.appendChild(eleDragParent);
                }else{
                    if(target.tagName=="LI"){
                        targetOl.insertBefore(eleDragParent, target);
                    }else if(targetParent.tagName=="LI"){
                        targetOl.insertBefore(eleDragParent, targetParent);
                    }else if(targetParents.tagName=="LI"){
                        targetOl.insertBefore(eleDragParent, targetParents);
                    }
                }
                return false;
            };
        },
        showStep:function(index){
            $(".stepPanel").addClass("hidden");
            $("#step"+index).removeClass("hidden");
            $("#step li").removeClass("active");
            $("#step li:eq("+(index-1)+")").addClass("active");
            $(window).scrollTop(0);
        },
        step1ToStep2Handler:function(){
            var thumb=$("#workThumb").val();
            var description=$("#workDescription").val().trim();
            var author=$("#workAuthor").val().trim();
            var title=$("#workTitle").val().trim();
            var attaCount=$("#attachmentRow .attachment").length;

            if(description&&author&&author!=";"&&title&&thumb&&attaCount!=0){

                this.showStep(2);

                if(!mediaUploader){
                    this.createMediaUploader();
                }
            }else if(!title){
                $().toastmessage("showErrorToast",config.message.workNoTitle);
            }else if(!description){
                $().toastmessage("showErrorToast",config.message.workNoDescription);
            }else if(!thumb){
                $().toastmessage("showErrorToast",config.message.workNoThumb);
            }else if(!author||author==";"){
                $().toastmessage("showErrorToast",config.message.workAuthorNoComplete);
            }else if(attaCount==0){
                $().toastmessage("showErrorToast",config.message.workNoAtta);
            }
        },
        step2ToStep3Handler:function(){
            if($("#mediaList li").length===0){
                $().toastmessage("showErrorToast",config.message.workNoMedia);
            }else{
                this.showStep(3);
                this.preview();
            }
        },
        addAuthorHandler:function(){
            var authorsLength=$("#authorList li").length;
            if(authorsLength<4){
                this.showAuthor();

                if(authorsLength+1===4){
                    $("#addAuthor").parent().addClass("hidden");
                }
            }
        },
        showAuthor:function(){
            var index=new Date().getTime();
            var tpl=$("#authorTpl").html();
            var html=juicer(tpl,{index:index});
            $("#authorList").append(html);

            this.createPhotoUploader(index);
        },
        deleteAuthorHandler:function(el){
            var index=el.attr("href");
            authorUploaders[index].destroy();
            authorUploaders[index]=undefined;
            delete authorUploaders[index];

            el.parents("li").remove();
            $("#addAuthor").parent().removeClass("hidden");
        },
        setMediaContentPanel:function(key){
            $("#mediaShow").attr("src",uploadedMedias[key]["image"]);
            $("#mediaDescription").val(uploadedMedias[key]["text"]);
            currentMediaKey=key;
        },
        clearMediaContentPanel:function(){
            $("#mediaShow").attr("src","images/app/defaultImg.jpg");
            $("#mediaDescription").val("");
        },
        setMediaFirstActive:function(){
            var first=$("#mediaList li:first");
            if(first.length!=0){
                var key=first.find(".file").attr("href");
                this.setMediaContentPanel(key);
                first.addClass("active");
            }
        },
        mediaClickHandler:function(el){
            $("#mediaList li").removeClass("active");
            el.parent().addClass("active");
            this.setMediaContentPanel(el.attr("href"));
        },
        deleteMedia:function(el){
            var key=el.parent().attr("href");
            uploadedMedias[key]=undefined;
            delete uploadedMedias[key];

            el.parents("li").remove();
            if(el.parents("li").hasClass("active")){
                this.setMediaFirstActive();
            }

            if($("#mediaList li").length==0){
                this.clearMediaContentPanel();
            }
        },
        setMediaDescription:function(value){
            uploadedMedias[currentMediaKey]["text"]=value.replace(/"/g,'\"');
        },
        initMedia:function(){
            var me=this;
            $.ajax({
                url:config.ajaxUrls.getWorkIntroduces.replace("{artifactId}",artifactId),
                dataType:"json",
                type:"get",
                success:function(data){
                    if(data){
                        var length=data.length;
                        var media=null;
                        for(var i=0;i<length;i++){
                            media=data[i];
                            uploadedMedias[media["pos"]]={
                                "image":media["image"],
                                "text":media["text"]
                            };
                        }

                        me.setMediaFirstActive();
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        }
    }
})(config);

$(document).ready(function(){

    //uploadWork.drag();
    uploadWork.createThumbUploader();
    uploadWork.createAttaUploader();

    //uploadWork.createMediaUploader();

    if(artifactId){
        uploadWork.initMedia();
    }


    //上一步和下一步按钮点击
    $("#step1ToStep2").click(function(event){
        uploadWork.step1ToStep2Handler(event);
    });
    $("#step2ToStep1").click(function(){
        uploadWork.showStep(1);
    });
    $("#step2ToStep3").click(function(){
        uploadWork.step2ToStep3Handler();
    });
    $(document).on("click","#step3ToStep2",function(){
        uploadWork.showStep(2);
    });

    //点击上传的图片,删除上传的图片
    $("#mediaList").on("click",".delete",function(){
        if(confirm(config.message.confirmDelete)){
            uploadWork.deleteMedia($(this));
        }

        return false;
    }).on("click",".file",function(){
            uploadWork.mediaClickHandler($(this));

            return false;
        });

    $("#mediaDescription").blur(function(){
        uploadWork.setMediaDescription($(this).val().trim());
    });

    //删除附件
    $("#attachmentRow").on("click","a.attachment",function(){
        if(confirm(config.message.confirmDelete)){
            $(this).remove();
        }


        return false;
    });

    //显示附件要求
    $("#attachmentRequire").click(function(){

        $("#popWindow,#blackout").removeClass("hidden");
        $("#blackout .loading").addClass("hidden");

        return false;
    });
    $("#closePopWindow,#blackout").click(function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");

        return false;
    });


    $("#uploadWorkForm").submit(function(){
        uploadWork.submitHandler($(this));

        return false;
    });
});