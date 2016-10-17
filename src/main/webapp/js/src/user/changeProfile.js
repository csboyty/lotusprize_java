/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-3-3
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
var changeProfile=(function(config){

    return {

        submitForm:function(form){
            config.showBlackout();
            $(form).ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        }

    };
})(config);

$(document).ready(function(){

    $("#changeProfileForm").validate({
        rules:{
            fullname:{
                required:true,
                maxlength:32
            },
            organization:{
                required:true,
                maxlength:32
            },
            mobile:{
                required:true,
                maxlength:20
            },
            address:{
                required:true,
                maxlength:64
            }
        },
        messages:{
            fullname:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",32)
            },
            organization:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",32)
            },
            mobile:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",20)
            },
            address:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",64)
            }
        },
        submitHandler:function(form) {
            changeProfile.submitForm(form);
        }
    });

});

