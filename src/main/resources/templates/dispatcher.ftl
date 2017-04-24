<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mailing</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="//code.jquery.com/jquery-2.2.4.min.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <style type="text/css">
        .form-style-4{
            margin: auto;
            width: 450px;
            font-size: 16px;
            background: #495C70;
            padding: 30px 30px 15px 30px;
            border: 5px solid #53687E;
        }
        .form-style-4 input[type=button],
        .form-style-4 input[type=file],
        .form-style-4 input[type=text],
        .form-style-4 input[type=email],
        .form-style-4 textarea,
        .form-style-4 label
        {
            font-family: Georgia, "Times New Roman", Times, serif;
            font-size: 16px;
            color: #fff;

        }
        .form-style-4 label {
            display:block;
            margin-bottom: 10px;
        }
        .form-style-4 label > span{
            display: inline-block;
            float: left;
            width: 150px;
        }
        .form-style-4 input[type=text],
        .form-style-4 input[type=email]
        {
            background: transparent;
            border: none;
            border-bottom: 1px dashed #83A4C5;
            width: 275px;
            outline: none;
            padding: 0px 0px 0px 0px;
            font-style: italic;
        }
        .form-style-4 textarea{
            font-style: italic;
            padding: 0px 0px 0px 0px;
            background: transparent;
            outline: none;
            border: none;
            border-bottom: 1px dashed #83A4C5;
            width: 275px;
            overflow: hidden;
            resize:none;
            height:20px;
        }

        .form-style-4 textarea:focus,
        .form-style-4 input[type=text]:focus,
        .form-style-4 input[type=email]:focus,
        .form-style-4 input[type=email] :focus
        {
            border-bottom: 1px dashed #D9FFA9;
        }

        .form-style-4 input[type=button],
        .form-style-4 input[type=file]{
            background: #576E86;
            border: none;
            padding: 8px 10px 8px 10px;
            border-radius: 5px;
            color: #A8BACE;
        }
        .form-style-4 input[type=button]:hover,
        .form-style-4 input[type=file]:hover{
            background: #394D61;
        }
    </style>
</head>

<body>
<form class="form-style-4" name="uploadForm" method="POST" action="javascript:void(null);" onsubmit="">
    <label for="to">
        <span>Email Address</span><input type="email" name="to" required="true" />
    </label>
    <label for="subject">
        <span>Subject</span><input type="text" name="subject" required="true" />
    </label>
    <label for="text">
        <span>Message</span><textarea name="text" onkeyup="adjust_textarea(this)" required="true"></textarea>
    </label>
    <label>
        <input id="fileInput" type="file" name="uploadingFiles" multiple
">
    </label>
    <label>
        <span>&nbsp;</span><input type="button" id="sendButton" value="Send Letter" />
    </label>
    <label id="log">
    </label>
</form>
<script type="text/javascript">

    function log(html) {
        $("#log").html(html);
    }

    function validateForm() {

        if (
            $("textarea[name=text]").val().length > 0 &&
            $("input[name=to]").val().length > 0 &&
            $("input[name=subject]").val().length> 0
        ) {
            return true;
        }
        else {
            log("All fields must be filled")
            return false;
        }
    }

    $("#sendButton").click(function () {
        if (!validateForm()) {
            return;
        }
        var mail = {};
        mail.text = $("textarea[name=text]").val();
        mail.to = $("input[name=to]").val();
        mail.subject = $("input[name=subject]").val();

        var files = document.getElementById('fileInput').files;

        var formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append("uploadingFiles[]", files[i]);
        }

        formData.append("mail", new Blob([JSON.stringify(mail)], {
            type: "application/json"
        }));

        $.when($.ajax(
                {
                    url: "/send",
                    type: "POST",
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false
                }
        ))
                .then(function (data) {
                    log("Wait...");
                    checker(data);
                }, function (data, statusText, xhr) {
                    if (data.status == 400) {
                        data.responseJSON.fieldErrors.forEach(function (f) {
                            log(f.message + "<br>");
                        });
                    } else if(data.status == 403){
                        log(data.responseText)
                    } else {
                        log("File too big!");
                    }
                    return false;
                });
    });


    function checker(id) {
        setTimeout(function run() {
            $.when($.ajax({
                type: "GET",
                url: "/get/" + id
            }))
                    .then(function (data, statusText, xhr) {
                        if (xhr.status == 200) {
                            log("OK!");
                        } else {
                            setTimeout(run, 1000)
                        }
                    }, function (data, statusText, xhr) {
                        log(xhr.status);
                        return false;
                    });
        }, 3000);
    }

    function adjust_textarea(h) {
        h.style.height = "20px";
        h.style.height = (h.scrollHeight)+"px";
    }
</script>
</body>
</html>