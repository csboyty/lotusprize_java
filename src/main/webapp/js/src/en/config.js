/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-21
 * Time: 上午10:22
 * To change this template use File | Settings | File Templates.
 */
var config={
    ajaxUrls:{
        getAllTopic:"s/topic/list?lang=en",
        getAllTopicAdmin:"s/admin/topic/list",
        getWorkJudgeRecord:"s/admin/artifact2expert/artifactScores",
        getAllUser:"s/admin/user/list",
        getAllWork:"s/admin/artifact/list",
        getJudgeScoreWork:"s/admin/topic/expert/artifact/list",
        getAllWorkJudge:"s/expert/topic/artifact/list",
        getAllWorkCorp:"s/topicManager/topic/artifact/list",
        getAllWorkCorpJudge:"s/topicManager/topic/artifact/listByScore",
        getAllWorkUser:"s/topic/artifact/list",
        getAllAwardsWork:"s/gallery/list",
        setWorkPraise:"s/topic/artifact/vote",
        setWorkStatusCorp:"s/topicManager/topic/artifact/setStatus",
        setWorkScore:"s/expert/topic/artifact/doScore",
        setWorkPrize:"s/admin/artifact/bindAwards",
        setWorkHatch:"s/admin/artifact/setHatch",
        topicBindJudge:"s/admin/artifact2expert/bind",
        deleteWorkPrize:"s/admin/artifact/unbindAwards",
        uploadFile:"/lotusprize/s/upload",
        deleteTopic:"s/topic/remove",
        deleteStage:"s/admin/stageSetting/remove",
        getUserDetail:"s/admin/user/info",
        getTopicIntroduces:"s/topic/view/{topicId}/introduce",
        getWorkIntroduces:"s/user/artifact/show/{artifactId}/introduce",
        getAllJudgeRecord:"s/admin/artifact2expert/list",
        getJudgeRecord:"s/admin/artifact2expert/expertStatus",
        deleteJudgeRecord:"s/admin/artifact2expert/unbind",
        deleteUser:"s/admin/user/remove",
        emailExistOrNot:"s/account/uniqueEmail",
        deleteWork:"s/user/artifact/remove"
    },
    emailUrls:{
        163:"http://mail.163.com",
        126:"http://mail.126.com",
        yeah:"http://www.yeah.net/",
        sina:"http://mail.sina.com.cn",
        yahoo:"http://mail.yahoo.com",
        sohu:"http://mail.sohu.com",
        gmail:"http://mail.google.com",
        hotmail:"http://www.hotmail.com" ,
        live:"http://www.hotmail.com",
        outlook:"http://www.hotmail.com",
        qq:"http://mail.qq.com",
        139:"http://mail.10086.cn"
    },
    pageUrl:{  //What is the current page
        workUpload:"s/work/upload",
        login:"s/login"
    },
    searchType:{
        title:"title",
        tag:"tag"
    },
    order:{
        DES:"DES",
        ASC:"ASC"
    },
    uploadSize:{
        img:"2m",
        attachment:"200m",
        video:"300m"
    },
    workStatus:{
        1:"Pending",
        2:"Through trial",
        3:"Trial failed",
        4:"进入一轮评审"
    },
    uploadFilter:{
        img:"jpg,jpeg,png",
        attachment:"zip,rar",
        video:"mp4"
    },
    maxFileNameLength:"50",
    categoryObj:{
        "1":" Intelligent Products and Service Design Innovation",
        "2":"Digital Manufacturing and Design Innovation",
        "3":"Social Service Design Innovation"
    },
    workAwards:{
        0:"No Award",
        1:"Netizen Award",
        2:"Innovative Design Award Bronze Prize",
        4:"Innovative Design Award Silver Prize",
        8:"Innovative Design Award Gold Prize",
        16:"Best Commercial Potential Award",
        32:"Future Star Award",
        64:"Best Visual Effects Award",
        128:"Best Social Innovation Award",
        256:"Best Intelligent Design Award",
        512:"Best Technology Innovation Award",
        1024:"Best of the Best",
        2048:"Honourable Mention",
        4096:"Best Design Application"
    },
    stage:{
        1:"命题发布",
        2:"征集作品",
        3:"资格审查",
        4:"网络公众投票",
        5:"一轮评审设置",
        6:"“创新设计优秀奖”评审",
        7:"二轮评审设置",
        8:"孵化项目评选及工作营"
    },
    message:{
        optSuccess:"Operation is successful!",
        progress:"In hand...",
        uploaded:"Uploaded!",
        confirmDelete:"Confirm to delete?",
        confirmDeleteWork:"Confirm to delete this work?",
        systemError:"System error,please do soon or email to mail@lotusprize.com!",
        networkError:"Network error,please try again later!",
        optSuccRedirect:"Operation is successful,After 3 seconds automatically jump to the management page!",
        timeout:"The login is timeout.After 3 seconds automatically jump to the login page!",
        optError:"Server-side exception，please try again later!",
        registerSuccess:"Registered successfully.Please enter the email address for account activation!",
        uploadSizeError:"The maximum file size ${value}!",
        uploadExtensionError:"Only allowed to upload ${value}!",
        filedMissing:"Not to fill out!",
        maxLength:"A character is most can fill in the number of characters!",
        notSquareImg:"Image is not 1:1 proportion!",
        emailExist:"Email has already been registered，please use another!",
        emailNotExist:"The email address does not exist!",
        oldPwdNotMatch:"Original password is not correct!",
        forgetPwdSuccess:"In the email，please enter email to retrieve password later!",
        topicNoMedia:"Subject does not upload images!",
        workNoTitle:"Work does not fill in the title!",
        workNoAtta:"Work does not upload an attachment!",
        workImageSizeError:"The image size is not A3 (1191x842,72dpi)!",
        workNoThumb:"Work does not upload a thumbnail image!",
        workNoDescription:"Work does not fill in the description!",
        workAuthorNoComplete:"The author information is not complete!",
        workNoMedia:"Work does not upload image files!",
        workMediaTooMuch:"The number of uploaded images is up to ${value}!",
        fileNameTooLong:"File name is too long.The maximum length is ${value} characters!",
        scoreError:"Please enter an integer between 0 to 100!"
    },
    invalidType:{
        valueMissing:"valueMissing",
        typeMismatch:"typeMismatch",
        patternMismatch:"patternMismatch",
        tooLong:"tooLong",
        rangeUnderflow:"rangeUnderflow",
        rangeOverflow:"rangeOverflow",
        stepMismatch:"stepMismatch"
    },
    ownInvalidMessage:{
        required:"This field must to be filled!",
        email:"Please fill a right email!",
        emailExist:"This email exists,please use another one!",
        rangelength:"This field must be ${min}-${max} characters!",
        maxlength:"The maximum number of characters is ${max}!",
        equalTo:"Two passwords are not the same!",
        agree:"Please choose the agreement of the contest!"
    },
    invalidMessage:{
        valueMissing:"This field will be filled!",
        typeMismatch:"The field type is not correct!Correct type of${value}",
        patternMismatch:"This field format is not correct!Correct type of${value}",
        tooLong:"The most attractive can fill in the fields of${value}",
        rangeUnderflow:"Minimum value for this field${value}",
        rangeOverflow:"Maximum value for this field${value}",
        stepMismatch:"This field must be${value}integer"
    },
    errorCode:{
        thumb_height_not_equals_width:"thumb_height_not_equals_width",
        work_image_size_illegal:"work_image_size_illegal",
        email_not_exist:"email_not_exist",
        timeout:"timeout",
        account_active_email_email_duplicate:"account_active_email_email_duplicate",
        topic_profile_size_illegal:"topic_profile_size_illegal",
        account_password_not_match:"account_password_not_match"
    },
    perLoadCount:{
        table:10,
        viewStep:10,
        view:100
    },
    showModalMessage:function(type,message){

        //On the basis of toast and a covering layer
    },
    getEmailDomain:function(email){
        var domain=email.substring(email.indexOf("@")+1);
        domain=domain.substring(0,domain.lastIndexOf("."));
        domain=domain.replace(/vip.|.com|.cn/g,"");
        return domain;
    },
    ajaxErrorHandler:function(){
        this.hideBlackout();
        $().toastmessage("showErrorToast",config.message.networkError);
    },
    ajaxReturnErrorHandler:function(data){
        this.hideBlackout();

        var content=this.message.optError;
        var errorCode=data.errorCode;
        switch(errorCode){
            case config.errorCode.thumb_height_not_equals_width:
                content=config.message.notSquareImg;

                break;
            case config.errorCode.timeout:
                content=config.message.timeout;

                break;
            case config.errorCode.email_not_exist:
                content=config.message.emailNotExist;

                break;
            case config.errorCode.account_password_not_match:
                content=config.message.oldPwdNotMatch;

                break;
            case config.errorCode.topic_profile_size_illegal:
                content=config.message.topicProfileSizeError;

                break;
            case config.errorCode.work_image_size_illegal:
                content=config.message.workImageSizeError;

                break;
            case config.errorCode.account_active_email_email_duplicate:
                content=config.message.emailExist;
                break;
            default:
                content=config.message.getDataError;
                break;
        }

        $().toastmessage("showErrorToast",content);
        if(errorCode==config.errorCode.timeout){
            this.redirect("s/login?_lang=en");
        }
    },
    createUploader:function(params){
        var me=this;
        var uploader = new plupload.Uploader({
            runtimes:"html5,flash",
            multi_selection:false,
            max_file_size:params.size,
            browse_button:params.btn,
            container:params.container,
            url:params.url,
            unique_names:true,
            flash_swf_url:'/lotusprize/js/lib/plupload.flash.swf',
            multipart_params:params.multipartParams,
            filters:[
                {title:"Media files", extensions:params.filter}
            ]
        });

        //initialize
        uploader.init();

        //File add event
        uploader.bind("FilesAdded", function (up, files) {
            if(params.fileAddCallback){
                params.fileAddCallback(up,files);
            }else{
                if(files[0].name.length>me.maxFileNameLength){

                    $().toastmessage("showErrorToast",me.message.fileNameTooLong.replace("${value}",me.maxFileNameLength));
                    //Delete the file
                    up.removeFile(files[0]);
                }else{
                    up.start();
                }
            }
        });

        uploader.bind("UploadProgress", function (up, file) {
            if(params.progressCallback){
                params.progressCallback(file.percent);
            }
        });

        //Error events
        uploader.bind("Error", function (up, err) {
            var message=err.message;
            if(message.match("Init")==null){
                if(message.match("size")){
                    $().toastmessage("showErrorToast",me.message.uploadSizeError.replace("${value}",params.size));
                }else if(message.match("extension")){
                    $().toastmessage("showErrorToast",me.message.uploadExtensionError.replace("${value}",params.filter));
                }else{
                    $().toastmessage("showErrorToast",me.message.optError);
                }
            }
            up.refresh();
        });

        //Uploaded event
        uploader.bind("FileUploaded", function (up, file, res) {
            params.callback(res,file.name);
        });


        return uploader;
    },
    initMenuStatus:function(url){
        if(url!==""){
            $("#menu a[href='"+url+"']").addClass("active");
        }
    },

    showBlackout:function(){
        $("#blackout").removeClass("hidden");
    },
    hideBlackout:function(){
        $("#blackout").addClass("hidden");
    },
    hideWorkPopWindow:function(){
        $("#popWindowContainer,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");
        $("body").css("overflowY","scroll");
        $("body").scrollTop(this.oldBodyScrollTop);
    },
    hidePopWindow:function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");
    },
    showInvalidMessage:function(el){
        var value="";
        var invalidType="";
        if(el.validity[this.invalidType.tooLong]){
            invalidType=this.invalidType.tooLong;
            value=el.maxLength;
        }else if(el.validity[this.invalidType.rangeUnderflow]){
            invalidType=this.invalidType.rangeUnderflow;
            value=el.min;
        }else if(el.validity[this.invalidType.rangeOverflow]){
            invalidType=this.invalidType.rangeOverflow;
            value=el.max;
        }else if(el.validity[this.invalidType.stepMismatch]){
            invalidType=this.invalidType.stepMismatch;
            value=el.step;
        }else if(el.validity[this.invalidType.typeMismatch]){
            invalidType=this.invalidType.typeMismatch;
            //value=el.title;浏览器会自动把title夹在后面
        }else if(el.validity[this.invalidType.valueMissing]){
            invalidType=this.invalidType.valueMissing;
        }else if(el.validity[this.invalidType.patternMismatch]){
            invalidType=this.invalidType.patternMismatch;
            //value=el.title;
        }

        if(invalidType){
            el.setCustomValidity(this.invalidMessage[invalidType].replace("${value}",value));
        }else{
            el.setCustomValidity("");
        }
    },
    redirect:function(url){
        setTimeout(function(){
            window.location.href=$("#baseUrl").attr("href")+url;
        },3000);
    },
    setContentHeight:function(){
        if(document.body.scrollHeight<=$("body").height()){
            $(".content").css("minHeight",$("body").height()-190);
        }

    },
    setDeadLine:function(){
        var deadLineTime=new Date("2014/07/07").getTime()+1000*60*60*24;
        var currentTime=new Date().getTime();
        var time = Math.ceil((deadLineTime-currentTime) / (1000 * 60 * 60 * 24));
        $("#deadLine").text(time);
    }
};

$(document).ready(function(){

    config.initMenuStatus(href);
    $("#statusTxt").text(function(index,text){
        return "当前阶段："+config.stage[text.trim()];
    });

    if($("#popWindowContainer").length!=0){
        $(document).keydown(function(e){
            if (e.which === 27) {
                config.hideWorkPopWindow();

                //return false;
            }
        });

        $("#closePopWindow").click(function(){
            config.hideWorkPopWindow();
            return false;
        });
        $("#popWindowContainer").click(function(event){
            var target=event.target||event.srcElement;
            if($(target).parents("#popWindow").length==0&&!$(target).is($("#popWindow"))){
                config.hideWorkPopWindow();
            }
        })
    }
    //config.setDeadLine();

    //config.setContentHeight();

    $(window).resize(function() {
        config.setContentHeight();
    });

    $("input[type='text'],input[type='email']").blur(function(){
        $(this).val($(this).val().trim());
    });
});

window.onload=function(){
    //考虑图片的加载

    config.setContentHeight();
};

