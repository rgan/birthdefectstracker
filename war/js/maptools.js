// Source from: http://gmaps-samples.googlecode.com/svn/trunk/poly/mymapstoolbar.html
var POLY_COLOR = "#008000";

function select(buttonId) {
    $("#hand_b").removeClass("selected");
    $("#shape_b").removeClass("selected");
    $("#placemark_b").removeClass("selected");
    $("#" + buttonId).addClass("selected");
}

function stopEditing() {
    select("hand_b");
}

function startShape() {
    select("shape_b");
    var polygon = new GPolygon([], POLY_COLOR, 2, 0.7, POLY_COLOR, 0.2);
    startDrawing(polygon, function() {
        EnvHazards.populateFormFieldsFromPoly(polygon)
    });
}

function erase() {
    map.clearOverlays();
}

function fullExtent() {
    map.setCenter(new GLatLng(0, 0), 1);
}

function populateFormFieldsWithLatLng(latlng) {
    $("input[name='person_lat']").val(latlng.lat())
    $("input[name='person_lon']").val(latlng.lng())
};


function placeMarker() {
    select("placemark_b");
    var listener = GEvent.addListener(map, "click", function(overlay, latlng) {
        if (latlng) {
            populateFormFieldsWithLatLng(latlng);
            select("hand_b");
            GEvent.removeListener(listener);
            var marker = new GMarker(latlng, { draggable: true});
            map.addOverlay(marker);
            GEvent.addListener(marker, "dragend", function() {
                populateFormFieldsWithLatLng(marker.getLatLng());
            });
        }
    });
}

function startDrawing(poly, onUpdate) {
    map.addOverlay(poly);
    poly.enableDrawing({});
    poly.enableEditing({onEvent: "mouseover"});
    poly.disableEditing({onEvent: "mouseout"});
    GEvent.addListener(poly, "endline", function() {
        select("hand_b");
        GEvent.bind(poly, "lineupdated", "", onUpdate);
        GEvent.addListener(poly, "click", function(latlng, index) {
            if (typeof index == "number") {
                poly.deleteVertex(index);
            } else {
                poly.setStrokeStyle({color: POLY_COLOR, weight: 4});
            }
        });
    });
}
