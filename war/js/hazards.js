var EnvHazards = function() {

    var polysCache = new OverlaysCache();

    var createHazardsResultsTable = function(hazards) {
        hazardPolys = [];
        var html = '<div id="searchResultsTable" class="searchresults">';
        $.each(hazards, function(i, item) {
            EnvHazards.addHazardToMap(item)
            html += EnvHazards.createTableRowForHazard(i, item);
        });
        html += '</div>';
        return html;
    };

    return {

        createTableRowForHazard: function(i, hazard) {
            return '<div>' + hazard.name + '</div><div>' + hazard.NAICTitle + '</div><div>' + truncateIfTooLong(hazard.description)
                    + '</div><div>' + EnvHazards.getMapLink(hazard)
                    + EnvHazards.getDeleteLink(i, hazard) + '</div>';
        },

        getMapLink : function(hazard) {
            return '<a href="javascript:EnvHazards.map(' + hazard.id + ')">Map</a>';
        },

        getDeleteLink : function(rowId, hazard) {
            return '<a href="javascript:EnvHazards.remove(\'' + rowId + '\',' + hazard.id + ')">Delete</a>';
        },

        remove: function(rowId, id) {
            clearAllMessages();
            showLoadingMessage();
            var data = ({ "id": id });
            var success = function(data, textStatus) {
                $('div').remove('#' + rowId);
                if (map) {
                    polysCache.clearFromMap(id, map);
                }
                hideLoadingMessage();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Error deleting hazard");
            };
            doAjax("POST", "deleteHazard.do", data, success, error);
        },

        addHazardToMap: function(hazard, latLngs)
        {
            if (map) {
                var poly = polyFromJson(hazard.vertices);
                polysCache.add(hazard.id, poly);
                map.addOverlay(poly);
                if (latLngs) {
                    $.each(hazard.vertices, function(i, vertex)
                    {
                        var latLng = new GLatLng(vertex.lat, vertex.lon);
                        latLngs.push(latLng);
                    });
                }
            }
        },

        populateFormFieldsFromPoly: function (poly) {
            var polyStr = polyToStr(poly);
            $("textarea[name='envhazard_vertices']").val(polyStr);
            var bounds = poly.getBounds();
            var bbox = bounds.getNorthEast().lat() + ',' + bounds.getNorthEast().lng() +
                       ',' + bounds.getSouthWest().lat() + ',' + bounds.getSouthWest().lng();
            $("input[name='envhazard_bbox']").val(bbox);
        },

        search: function () {
            $('#search_results').html('');
            clearAllMessages();
            showLoadingMessage();
            $.ajax({
                type: "GET",
                url: "searchHazards.do",
                timeout: 10000,
                data: ({
                    "text" : $("input[id='search_hazards_text']").val()
                }),
                success: function(data, textStatus) {
                    $('#search_results').html(createHazardsResultsTable(jsonParse(data)));
                    hideLoadingMessage();
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    hideLoadingMessage();
                    $('#search_errors').append("<font color='red'>Error occurred.</font><br/>");
                }
            });
        },



        save : function() {
            clearAllMessages();
            showLoadingMessage();
            $.ajax({
                type: "POST",
                url: "saveEnvHazard.do",
                timeout: 10000,
                data: ({
                    "name" : $("input[name='envhazard_name']").val(),
                    "vertices" : $("textarea[name='envhazard_vertices']").val(),
                    "description" : $("textarea[name='envhazard_desc']").val(),
                    "boundingBox" : $("input[name='envhazard_bbox']").val(),
                    "naicCode" : $("input[name='envhazard_naic']").val()
                }),
                success: function(data, textStatus) {
                    $("input[name='envhazard_bbox']").val('');
                    $("input[name='envhazard_name']").val('');
                    $("textarea[name='envhazard_vertices']").val('');
                    $("textarea[name='envhazard_desc']").val('');
                    $("input[name='envhazard_naic']").val('');
                    $('#add_envhazard_errors').append("<font color='green'>Saved.</font><br/>");
                    hideLoadingMessage();
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    showError(XMLHttpRequest.responseText, $('#add_envhazard_errors'));
                }
            });
        },

        map : function (id) {
            clearAllMessages();
            $.ajax({
                type: "GET",
                url: "hazard.do",
                data: ({ "id": id }),
                timeout: 10000,
                success: function(data, textStatus) {
                    var hazard = jsonParse(data);
                    if (map) {
                        map.setCenter(new GLatLng(hazard.bboxCenter.lat, hazard.bboxCenter.lon), DEFAULT_ZOOM_LEVEL);
                        EnvHazards.addHazardToMap(hazard);
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert("Error retrieving hazard");
                }
            });
        }
    }
}();