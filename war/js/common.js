var map;
var searchControl;
var DEFAULT_ZOOM_LEVEL = 7;
var DETAIL_ZOOM_LEVEL = 10;
var INITIAL_ZOOM_LEVEL = 1;
var ONE_BY_ONE_SUMMARY_ZOOM_LEVEL = 5;
var TOOLS_ENABLED_ZOOM_LEVEL = 7;

function load() {
    if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map"));
        map.setCenter(new GLatLng(0, 0), INITIAL_ZOOM_LEVEL);
        map.addControl(new GSmallMapControl());
        map.addControl(new GMapTypeControl());
        searchControl = new DragZoomControl({}, {buttonHTML: "Search .."}, {dragend: spatialSearch});
        map.addControl(searchControl);
        map.clearOverlays();
        GEvent.addListener(map, "zoomend", function(oldzoom, zoom) {
            Persons.showSummaries(zoom);
            enableDisableMapTools(zoom);
        });
        //Persons.showSummaries(INITIAL_ZOOM_LEVEL);
    }
}

function enableDisableMapTools(zoom) {
    if (zoom >= TOOLS_ENABLED_ZOOM_LEVEL) {
        if ($('#addPerson').is(":visible")) {
            $('#placemark_b').show();
            $('#shape_b').hide();
        }
        if ($('#addEnvHazard').is(":visible")) {
            $('#shape_b').show();
            $('#placemark_b').hide();
        }
    } else {
        $('#shape_b').hide();
        $('#placemark_b').hide();
    }
}

function showLoadingMessage() {
    $('#loadingMessage').html("Please wait..");
}

function hideLoadingMessage() {
    $('#loadingMessage').html("");
}

function createSpatialDownloadLink(nelatlng, swlatlng) {
    var url = "spatialSearchDownload.do?nelat=" + escape(nelatlng.lat()) + "&nelng="
            + escape(nelatlng.lng()) + "&swlat=" + escape(swlatlng.lat())
            + "&swlng=" + escape(swlatlng.lng());
    return '<a href="' + url + '">Download</a>';
}

function spatialSearch(nwlatlng, nelatlng, selatlng, swlatlng, nwpt, nept, sept, swpt) {
    $('#spatial_search_results').html('');
    clearAllMessages();
    showLoadingMessage();
    $.ajax({
        type: "GET",
        url: "spatialSearch.do",

        data: ({ "nelat": nelatlng.lat(),
            "nelng" : nelatlng.lng(),
            "swlat" : swlatlng.lat(),
            "swlng" : swlatlng.lng()
        }),
        success: function(data, textStatus) {
            $('#search_results').html(Persons.createResultsTable(jsonParse(data), createSpatialDownloadLink(nelatlng, swlatlng), 'Search Results'));
            hideLoadingMessage();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            hideLoadingMessage();
            $('#search_errors').append("<font color='red'>Error occurred.</font><br/>");
        }
    });
}

function showError(responseText, errorDiv) {
    hideLoadingMessage();
    var msg = responseText;
    if (msg.length > 200) {
        msg = "Error saving form. </br>Make sure required fields are correctly entered."
    }
    errorDiv.append("<font color='red'>" + msg + "</font><br/>");

}

function enablePersonForm() {
    $('#addPerson').show();
    enableDisableMapTools(map.getZoom());
}

function disablePersonForm() {
    $('#addPerson').hide();
    enableDisableMapTools(map.getZoom());
}

function clearAllMessages() {
    $('#add_birth_defect_errors').html('');
    $('#add_person_errors').html('');
    $('#search_errors').html('');
    $('#add_envhazard_errors').html('');
}

function loggedIn() {
    $.ajax({
        type: "GET",
        url: "loggedIn.do",
        data: "",
        timeout: 10000,
        success: function(data, textStatus) {
            doPostLoginLogout(true, data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            doPostLoginLogout(false, '');
        }
    });
}

function login() {
    clearAllMessages();
    $.ajax({
        type: "POST",
        url: "login.do",
        timeout: 10000,
        data: ({
            "username" : $("input[name='username']").val(),
            "password" : $("input[name='password']").val()
        }),
        success: function(data, textStatus) {
            var username = $("input[name='username']", $("#loginForm")).val();
            $.modal.close();
            $('#logout_button').show();
            doPostLoginLogout(true, username);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("Invalid credentials.")
        }
    });
}

function logout() {
    clearAllMessages();
    $.ajax({
        type: "POST",
        url: "logout.do",
        data: "foo",
        timeout: 10000,
        success: function(data, textStatus) {
            $('#logout_button').hide();
            doPostLoginLogout(false);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("Error logging out.")
        }
    });
}

function doPostLoginLogout(isLoggedIn, username) {
    clearAllMessages();
    if (map) {
        map.clearOverlays();
        Persons.showSummaries(map.getZoom());
    }
    $('#spatial_search_results').html('');
    $('#search_results').html('');
    if (isLoggedIn) {
        $('#person_menu').show();
        $('#hazards_menu').show();
        $('#logout_button').show();
        $('#loggedInMessage').html("Welcome, " + username);
        enablePersonForm();
    } else {
        $('#logout_button').hide();
        $('#person_menu').hide();
        $('#hazards_menu').show();
        $('#loggedInMessage').html("");
        disablePersonForm();
    }
}

$(document).ready(function() {
    $('#shape_b').hide();
    $('#placemark_b').hide();
    $("input[id='search_fromdate']").val('1980-01-01')
    $("input[id='search_todate']").val(today())
    $("#person_dateOfBirth").datepicker({
        changeMonth: true,
        changeYear: true
    });
    $('#person_dateOfBirth').datepicker('option', {dateFormat: 'yy-mm-dd'})
    $("#search_fromdate").datepicker({
        changeMonth: true,
        changeYear: true
    });
    $('#search_fromdate').datepicker('option', {dateFormat: 'yy-mm-dd'})
    $("#search_todate").datepicker({
        changeMonth: true,
        changeYear: true
    });
    $('#search_todate').datepicker('option', {dateFormat: 'yy-mm-dd'})
    BirthDefects.populateDropdowns([$("select[name='person_defects']"),
        $("select[name='search_defects']")]);
    loggedIn();
    $("#envhazard_naic").autocomplete("searchNAIC.do", {
        minChars: 4,
        width: 260,
        selectFirst: false
    });
});