<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Uploading Files Example with Spring Boot, Freemarker</title>
</head>

<body onload="updateSize();">
<form name="mail" enctype="multipart/form-data" action="/" method="POST">

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
        <input id="fileInput" type="file" name="uploadingFiles" onchange="updateSize();" multiple>
        selected files: <span id="fileNum">0</span>;
        total size: <span id="fileSize">0</span>
    </p>
    <p>
        <input type="submit" value="Send message">
    </p>

    <div id="log"></div>
</form>
<script>
    function updateSize() {
        var nBytes = 0,
                oFiles = document.getElementById("fileInput").files,
                nFiles = oFiles.length;
        for (var nFileId = 0; nFileId < nFiles; nFileId++) {
            nBytes += oFiles[nFileId].size;
        }
        var sOutput = nBytes + " bytes";
        // optional code for multiples approximation
        for (var aMultiples = ["KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"], nMultiple = 0,
                     nApprox = nBytes / 1024; nApprox > 1; nApprox /= 1024, nMultiple++) {
            sOutput = nApprox.toFixed(3) + " " + aMultiples[nMultiple] + " (" + nBytes + " bytes)";
        }
        // end of optional code
        document.getElementById("fileNum").innerHTML = nFiles;
        document.getElementById("fileSize").innerHTML = sOutput;
    }

    function log(html) {
        document.getElementById('log').innerHTML = html;
    }

    document.forms.mail.onsubmit = function (f) {
        var files = this.elements.uploadingFiles.files;
        var mail = {
            subject: this.elements.subject,
            to: this.elements.to,
            text: this.elements.text
        };
        if (files) {
            upload(mail, files);
        }
        return false;
    };


    function upload(mail, files) {

        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function () {
            if (this.readyState == 4) {
                if (this.status == 201) {
                    log("Wait...");
                    var id = this.responseText;
                    checker(id);
                } else if () {
                    log("error " + this.status);
                }
            }
        };

        var formData = new FormData();
        var len = document.getElementById('file').files.length;
        for (var i = 0; i < len; i++) {
            formData.append("file" + i, document.getElementById('file').files[i]);
        }
        formData.append("mail", new Blob([JSON.stringify(mail)], {
            type: "application/json"
        }));
        xhr.open("POST", "/", true);
        xhr.send(formData);
    }

    function checker(id) {
        var intervalID = setInterval(function () {
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (this.readyState == 4) {
                    if (this.status == 200) {
                        log("OK!");
                        clearInterval(intervalID);
                    } else if (this.status == 403) {
                        log("Forbidden!");
                        clearInterval(intervalID);
                    }
                }
            };
            xhr.open("GET", "/" + id, true);
            xhr.send();
        }, 1000)
    }
</script>
</body>
</html>