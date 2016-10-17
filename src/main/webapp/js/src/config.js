/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-21
 * Time: 上午10:22
 * To change this template use File | Settings | File Templates.
 */
var config={
    ajaxUrls:{
        getAllTopic:"s/topic/list",
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
        setWorkStatus:"s/admin/artifact/setStatus",
        setWorkStatusCorp:"s/topicManager/topic/artifact/setStatus",
        setWorkScore:"s/expert/topic/artifact/doScore",
        setWorkPrize:"s/admin/artifact/bindAwards",
        setWorkHatch:"s/admin/artifact/setHatch",
        setWorkRound:"s/admin/artifact/setRound",
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
    pageUrl:{  //判断当前是哪一页
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
    uploadFilter:{
        img:"jpg,jpeg,png",
        attachment:"zip,rar",
        pdf:"pdf",
        video:"mp4"
    },
    maxFileNameLength:"50",
    categoryObj:{
        "1":"A1 数字化制造与设计创新",
        "2":"A2 智能产品与服务设计创新",
        "3":"B 社会化服务设计创新"
    },
    workStatus:{
        1:"待审核",
        2:"审核通过",
        3:"审核未通过",
        4:"进入一轮评审"
    },
    workAwards:{
        0:"未获奖",
        1:"网络人气奖",
        2:"创新设计优秀奖铜奖",
        4:"创新设计优秀奖银奖",
        8:"创新设计优秀奖金奖",
        16:"最佳商业潜力奖",
        32:"未来之星奖",
        64:"最佳视觉效果奖",
        128:"最佳社会创新奖",
        256:"最佳智能设计奖",
        512:"最佳科技创新奖",
        1024:"至尊大奖",
        2048:"优秀奖",
        4096:"最佳创新应用奖"
    },
    stage:{
        1:"命题发布",
        2:"征集作品",
        3:"资格审查",
        4:"网络公众投票",
        5:"一轮评审设置",
        6:"“创新设计优秀奖”评审",
        7:"二轮评审设置",
        8:"孵化项目评选及工作营",
        9:"终评"
    },
    message:{
        optSuccess:"操作成功！",
        progress:"处理中...",
        judgeLength:"有且只有3个评委！",
        uploaded:"上传完成！",
        confirmDelete:"确定删除吗？",
        confirmDeleteWork:"确定删除作品？",
        judgeRecordNoJudge:"没有选择评委！",
        noSelected:"没有选中任何记录！",
        judgeRecordNoWork:"没有选择作品！",
        networkError:"网络异常，请稍后重试！",
        systemError:"系统错误，请稍后重试或者联系mail@lotusprize.com！",
        topicHasAdded:"此选题的作品已经添加！",
        optSuccRedirect:"操作成功,3秒后跳转到管理页！",
        timeout:"登录超时，3秒后自动跳到登陆页！",
        optError:"服务器端异常，请稍后重试！",
        registerSuccess:"注册成功，请进入邮箱进行账户激活！",
        uploadSizeError:"最大文件大小${value}！",
        uploadExtensionError:"只允许上传${value}！",
        filedMissing:"没有填写！",
        maxLength:"个字符为最多可填写字符数！",
        notSquareImg:"图片不是1:1比例！",
        topicProfileSizeError:"图片尺寸不是300x200！",
        emailExist:"邮箱已经存在，请使用其他邮箱！",
        emailNotExist:"您输入的邮箱不存在！",
        oldPwdNotMatch:"原始密码不正确！",
        forgetPwdSuccess:"邮件发送中，请稍后进入邮箱进行找回密码！",
        topicNoMedia:"课题没有上传图片说明！",
        workNoTitle:"作品没有填写标题！",
        workImageSizeError:"图片不是A3大小（1191x842,72dpi）！",
        workNoAtta:"作品没有上传附件！",
        workNoThumb:"作品没有上传缩略图！",
        workNoDescription:"作品没有填写描述！",
        workAuthorNoComplete:"作者信息还没有填写完整！",
        workNoMedia:"作品没有上传图片文件！",
        workMediaTooMuch:"最多可上传${value}张图片！",
        fileNameTooLong:"文件名过长，最大长度为${value}个字！",
        scoreError:"请输入0-100的任意整数！"
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
        required:"此字段必填！",
        email:"请填写正确的邮箱！",
        emailExist:"邮箱已经被注册，请更换邮箱！",
        rangelength:"此字段为${min}-${max}个字符！",
        maxlength:"此字段最多为${max}个字符！",
        equalTo:"两次输入的密码不一致！",
        agree:"请同意本次大赛的协议！"
    },
    invalidMessage:{
        valueMissing:"此字段必填！",
        typeMismatch:"此字段类型不正确！正确类型为${value}",
        patternMismatch:"此字段格式不正确！正确格式为${value}",
        tooLong:"此字段最大可填写长度为${value}",
        rangeUnderflow:"此字段最小值为${value}",
        rangeOverflow:"此字段最大值为${value}",
        stepMismatch:"此字段必须是${value}的整数"
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
    oldBodyScrollTop:0,
    perLoadCount:{
        table:10,
        viewStep:10,
        view:100
    },
    showModalMessage:function(type,message){

        //在toast的基础上加一个遮盖层
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
                content=config.message.systemError;
                break;
        }

        $().toastmessage("showErrorToast",content);
        if(errorCode==config.errorCode.timeout){
            this.redirect("s/login");
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

        //初始化
        uploader.init();

        //文件添加事件
        uploader.bind("FilesAdded", function (up, files) {
            if(params.fileAddCallback){
                params.fileAddCallback(up,files);
            }else{
                if(files[0].name.length>me.maxFileNameLength){

                    $().toastmessage("showErrorToast",me.message.fileNameTooLong.replace("${value}",me.maxFileNameLength));
                    //删除文件
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

        //出错事件
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

        //上传完毕事件
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
    //config.setDeadLine();

    //config.setContentHeight();

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

