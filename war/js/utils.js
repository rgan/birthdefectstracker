var MAX_FIELD_SIZE_FOR_REPORTS = 40

function doAjax(method, url, data, success, error) {
    $.ajax({
        type: method,
        url: url,
        data: data,
        dataFilter: function(data, type) {
            try {
                return jsonParse(data);
            } catch (e) {
                return data;
            }
        },
        timeout: 50000,
        cache: false,
        success: success,
        error: error
    });
}

function today()
{
    var d = new Date();
    return d.getFullYear() + '-' + d.getMonth() + '-' + d.getDate();
}

function formatNumber(num)
{
    var numStr = num + '';
    var intPortion = numStr.substr(0, numStr.indexOf('.'));
    var decimalPortion = num - intPortion;
    return intPortion + '.' + Math.round(decimalPortion * 100);
}

function truncateIfTooLong(str) {
    if (str.length > MAX_FIELD_SIZE_FOR_REPORTS) {
        return str.substr(0, MAX_FIELD_SIZE_FOR_REPORTS) + '...';
    }
    return str;
}

function openWindow(url) {
    window.open(url, '', 'scrollbars=yes,menubar=no,height=600,width=800,resizable=yes,toolbar=no,location=no,status=no');
}

function boundsFromLatLngs(latLngs)
{
    var neLat = -180
    var neLon = -90
    var swLat = 180
    var swLon = 90
    $.each(latLngs, function(i, item) {
        if (item.lat() > neLat) {
            neLat = item.lat()
        }
        if (item.lng() > neLon) {
            neLon = item.lng();
        }
        if (item.lat() < swLat) {
            swLat = item.lat();
        }
        if (item.lng() < swLon) {
            swLon = item.lng();
        }
    });
    return new GLatLngBounds(new GLatLng(swLat, swLon), new GLatLng(neLat, neLon))
}

function polyToStr(poly) {
    var vertices = "";
    for (i = 0; i < poly.getVertexCount(); i++) {
        var vertex = poly.getVertex(i);
        vertices += vertex.lat() + ',' + vertex.lng();
        if (i + 1 < poly.getVertexCount()) {
            vertices += ';';
        }
    }
    return vertices;
}

function polyFromJson(vertices, color) {
    var latlngs = [];
    $.each(vertices, function(i, item) {
        latlngs.push(new GLatLng(item.lat, item.lon));
    });
    return new GPolygon(latlngs, (color != null) ? color : POLY_COLOR);
}

function polyFromBounds(neLat, neLng, swLat, swLng, color) {
    var latlngs = [];
    latlngs.push(new GLatLng(neLat, neLng));
    latlngs.push(new GLatLng(neLat, swLng));
    latlngs.push(new GLatLng(swLat, swLng));
    latlngs.push(new GLatLng(swLat, neLng));
    latlngs.push(new GLatLng(neLat, neLng));
    return new GPolygon(latlngs, (color != null) ? color : POLY_COLOR);
}
;


