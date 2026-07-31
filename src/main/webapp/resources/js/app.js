function downloadExcelXHR(linkId, fileName) {
    var link = document.getElementById(linkId);
    if (!link) {
        return false;
    }
    var form = link.closest ? link.closest('form') : (function (el) {
        while (el && el.tagName !== 'FORM') { el = el.parentNode; }
        return el;
    })(link);
    if (!form) {
        return false;
    }

    if (window.PF && PF('statusDialog')) {
        PF('statusDialog').show();
    }

    var formData = new FormData(form);
    formData.append(linkId, linkId);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.action, true);
    xhr.responseType = 'blob';

    xhr.onload = function () {
        if (window.PF && PF('statusDialog')) {
            PF('statusDialog').hide();
        }
        var contentType = xhr.getResponseHeader('Content-Type') || '';
        var isHtml = contentType.indexOf('text/html') !== -1;
        if (xhr.status === 200 && !isHtml && xhr.response && xhr.response.size > 0) {
            var url = URL.createObjectURL(xhr.response);
            var a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = (fileName || 'export') + '.xls';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            setTimeout(function () { URL.revokeObjectURL(url); }, 1000);
        } else {
            alert('No se pudo generar el archivo. Es posible que la sesión haya expirado.');
        }
    };

    xhr.onerror = function () {
        if (window.PF && PF('statusDialog')) {
            PF('statusDialog').hide();
        }
        alert('Error de conexión al exportar.');
    };

    xhr.send(formData);
    return false;
}
