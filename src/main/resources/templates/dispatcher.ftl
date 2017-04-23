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
        .center {
            margin: auto;
        }
        .frame {
            width: 50%;
            border: 3px solid #e6a95b;
            padding: 10px;
        }
    </style>
</head>

<body>
<div class="center frame"
    <form name="uploadForm" method="POST" action="javascript:void(null);" onsubmit="">

        <p class="email center"><b>Email of recipient: </b><br>
            <input type="text" name="to" placeholder="example@gmail.com"
                   maxlength="30" required/>
        </p class="subject center">
        <p><b>Theme: </b><br>
            <input type="text" name="subject" placeholder="The best message dispatcher"
                   maxlength="30" required/>
        </p>
        <p class="message center">Message:<Br>
            <textarea name="text" placeholder="I use the best message dispatcher in the world!"
                      maxlength="300" cols="40" rows="3" required/></textarea>
        </p>
        <p class="fileInput center">
            <input id="fileInput" type="file" name="uploadingFiles" multiple>
        </p>
        <p class="sendButton center">
            <button id="sendButton"> Send </button>
        </p>
        </p>

        <div id="log">____________________________________</div>
    </form>
</div>
<script type="text/javascript">

    function log(html) {
        document.getElementById('log').innerHTML = html;
    }

    function error(html) {
        document.getElementById('log').innerHTML = <font color="red" face="Arial">html</font>;
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
            error("All fields must be filled")
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
                            error(f.message + "<br>");
                        });
                    } else {
                        error("Error");
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
                        error(xhr.status);
                        return false;
                    });
        }, 3000);
    }
</script>
</body>
</html>