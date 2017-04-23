<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="//code.jquery.com/jquery-2.2.4.min.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <meta charset="UTF-8">
    <title>Mailing</title>
</head>

<body>
<form name="uploadForm" method="POST" action="javascript:void(null);" onsubmit="">

    <p><b>Email of recipient: </b><br>
        <input type="text" name="to" placeholder="example@gmail.com"
               maxlength="30" required/>
    </p>
    <p><b>Theme: </b><br>
        <input type="text" name="subject" placeholder="The best message dispatcher"
               maxlength="30" required/>
    </p>
    <p>Message:<Br>
        <textarea name="text" placeholder="I use the best message dispatcher in the world!"
                  maxlength="300" cols="40" rows="3" required/></textarea>
    </p>
    <p>
        <input id="fileInput" type="file" name="uploadingFiles" multiple>
    </p>
    <p>
        <button id="valera">Send message</button>
    </p>

    <div id="log">____________________________________</div>
</form>
<script type="text/javascript">

    function log(html) {
        document.getElementById('log').innerHTML = html;
    }

    $("#valera").click(function () {
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
                }, function (xhr, status) {
                    log(xhr.responseText + "-" + status);
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
                        log(xhr.status );
                        return false;
                    });
        }, 3000);
    }
</script>
</body>
</html>