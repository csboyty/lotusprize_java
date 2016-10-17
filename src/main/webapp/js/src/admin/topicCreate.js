/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
var topicCreate=(function(config){

    var uploadedMedias={

    };
    var currentMediaKey="";
    var mediaUploader=null;
    return {
        createThumbUploader:function(){
            config.createUploader({
                size:config.uploadSize.img,
                filter:config.uploadFilter.img,
                btn:"uploadThumb",
                multipartParams:{
                    isTopicProfile:true
                },
                container:"uploadThumbContainer",
                url:config.ajaxUrls.uploadFile,
                callback:function(res,filename){
                    var response=JSON.parse(res.response);
                    if(response.success){
                        $("#topicThumb").val(response.uri);
                        $("#topicThumbShow").attr("src",response.uri);
                    }else{
                        config.ajaxReturnErrorHandler(response);
                    }
                }
            });
        },
        createLogoUploader:function(){
            config.createUploader({
                size:config.uploadSize.img,
                filter:config.uploadFilter.img,
                btn:"uploadLogo",
                multipartParams:{
                    isThumb:true
                },
                container:"uploadLogoContainer",
                url:config.ajaxUrls.uploadFile,
                callback:function(res,filename){
                    var response=JSON.parse(res.response);
                    if(response.success){
                        $("#topicLogo").val(response.uri);
                        $("#topicLogoImg").attr("src",response.uri);
                    }else{
                        config.ajaxReturnErrorHandler(response);
                    }
                }
            });
        },
        createVideoUploader:function(){
            config.createUploader({
                size:config.uploadSize.video,
                filter:config.uploadFilter.video,
                btn:"uploadVideo",
                container:"uploadVideoContainer",
                url:config.ajaxUrls.uploadFile,
                callback:function(res,filename){
                    var response=JSON.parse(res.response);
                    if(response.success){
                        var tpl=$("#videoTpl").html();
                        var html=juicer(tpl,{
                            filename:filename,
                            filepath:response.uri
                        });
                        $("#videoRow .attachment").remove();
                        $("#videoRow").append(html);
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
        createMediaUploader:function(){
            var me=this;
            mediaUploader=config.createUploader({
                size:"10m",
                filter:config.uploadFilter.img,
                btn:"uploadMedia",
                container:"uploadMediaContainer",
                url:config.ajaxUrls.uploadFile,
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

        showStep:function(index){
            $(".stepPanel").addClass("hidden");
            $("#step"+index).removeClass("hidden");
            $("#step li").removeClass("active");
            $("#step li:eq("+(index-1)+")").addClass("active");
            $(window).scrollTop(0);
        },
        step1ToStep2Handler:function(event){

            var logo=$("#topicLogo").val();
            var formValid=$("#topicCreateForm")[0].checkValidity();
            var thumb=$("#topicThumb").val();
            if(formValid&&logo){

                this.showStep(2);

                //只有表单通过了验证的情况下，才阻止默认事件（针对提交），不然会阻止表单验证
                event.preventDefault();
                event.stopPropagation();

                if(!mediaUploader){
                    this.createMediaUploader();
                }
            }else if(formValid&&(!logo||!thumb)){

                $().toastmessage("showErrorToast","企业logo、封面图"+config.message.filedMissing);

                //只有表单通过了验证的情况下，才阻止默认事件（针对提交），不然会阻止表单验证
                event.preventDefault();
                event.stopPropagation();
            }
        },
        step2ToStep3Handler:function(){
            if($("#mediaList li").length===0){
                $().toastmessage("showErrorToast",config.message.topicNoMedia);
            }else{
                this.showStep(3);
                this.preview();
            }
        },
        setHeight:function(){
            $(".imgInfo").each(function(index,el){
                var $el=$(el);
                if($el.find(".imgInfoContent").height()<=75){
                    $el.find(".showMore").remove();
                    $el.height("auto");
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
                topicTitle:$("#topicTitle").val(),
                medias:medias,
                topicCategory:config.categoryObj[$("#topicCategory").val()],
                topicLogo:$("#topicLogo").val(),
                topicMoney:$("#topicMoney").val(),
                topicCompanyName:$("#topicCompanyName").val(),
                topicOtherAward:$("#topicOtherAward").val(),
                topicAttDown:$("#attachmentRow .attachment input").val(),
                topicVideo:$("#videoRow .attachment input").val(),
                topicRewardQuota:$("#topicRewardQuota").val(),
                topicDescription:$("#topicDescription").val()
            };
            var html=juicer(tpl,data);
            $("#step3").html(html);
            this.setHeight();
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
        setMediaContentPanel:function(key){
            $("#mediaShow").attr("src",uploadedMedias[key]["image"]);
            $("#mediaDescription").val(uploadedMedias[key]["text"]);
            currentMediaKey=key;
        },
        clearMediaContentPanel:function(){
            $("#mediaShow").attr("src","images/app/defaultImg.jpg");
            $("#mediaDescription").val("");
        },
        initMedia:function(){
            var me=this;
            $.ajax({
                url:config.ajaxUrls.getTopicIntroduces.replace("{topicId}",topicId),
                dataType:"json",
                type:"get",
                data:{
                    lang:lang
                },
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
                        config.redirect("s/topic/mgr");
                    }else{
                        config.ajaxReturnErrorHandler(data);
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

    //topicCreate.drag();
    topicCreate.createAttaUploader();
    topicCreate.createLogoUploader();
    topicCreate.createThumbUploader();
    topicCreate.createVideoUploader();
    //topicCreate.createMediaUploader();

    if(topicId){
        topicCreate.initMedia();
    }

    //上一步和下一步按钮点击
    $("#step1ToStep2").click(function(event){
        topicCreate.step1ToStep2Handler(event);
    });
    $("#step2ToStep1").click(function(){
        topicCreate.showStep(1);
    });
    $("#step2ToStep3").click(function(){
        topicCreate.step2ToStep3Handler();
    });
    $(document).on("click","#step3ToStep2",function(){
        topicCreate.showStep(2);
    });

    //删除上传的图片
    $("#mediaList").on("click",".delete",function(){
        topicCreate.deleteMedia($(this));

        return false;
    }).on("click",".file",function(){
            topicCreate.mediaClickHandler($(this));

            return false;
        });

    $("#mediaDescription").blur(function(){
        topicCreate.setMediaDescription($(this).val().trim());
    });

    $("#topicCreateForm").submit(function(){

        topicCreate.submitHandler($(this));

        return false;
    });

    $("#attachmentRow,#videoRow").on("click","a.attachment",function(){
        $(this).remove();

        return false;
    });

    //显示描述
    $(document).on("mouseover",".showMore",function(){
          if($(this).parent().height()<$(this).siblings().height()){
              $(this).parent().height("auto");
          }
    }).on("mouseout",".showMore",function(){
            $(this).parent().height("75px");
        })

});
