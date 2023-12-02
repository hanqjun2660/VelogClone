var Utils = {
    request : function (paramMap, successCallBackFunc) {

        if(Utils.isNull(paramMap)) {
            console.warn('parameter가 존재하지 않습니다.');
        }

        if(Utils.isNull(paramMap['dataParam'])) {
            paramMap['dataParam']	= {};
        }

        url = paramMap['url'];

        if(Utils.isNull(url)){
            console.warn('url 이 존재 하지 않습니다.');
            return;
        }

        contentType = paramMap['contentType'];

        if(Utils.isNull(contentType)){
            dataParam = JSON.stringify(paramMap['dataParam']);
            contentType = 'application/json; charset=utf-8';
        } else {
            dataParam = paramMap['dataParam'];
            contentType = contentType;
        }

        $.ajax({
            type : 'POST',
            url : url,
            contentType : contentType,
            data : dataParam,
            async : true,
            success : function (response) {
                if(!Utils.isNull(response) && !Utils.isNull(successCallBackFunc) && typeof(successCallBackFunc) == 'function') {
                    successCallBackFunc(response);
                } else if(Utils.isNull(successCallBackFunc)) {
                    console.error('Callback function이 없습니다.');
                } else {
                    console.error('실패');
                }
            },
            error : function (e) {
                console.error(e);
            }
        });
    },
    /**
     * data가 존재하는지 여부 체크
     * @param data
     * @returns {boolean}
     */
    isNull : function (data) {
        if(data === undefined ||  data === 'undefined'
            || data === null || data === 'null' || (typeof(data) == 'string' && data.replace(/(^\s*)|(\s*$)/gi, '') === '')
            || (data != null && typeof(data) == 'object' && !Object.keys(data).length) ){
            return true;
        } else {
            return false;
        }
    }
}