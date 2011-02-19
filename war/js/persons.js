var Persons = function() {
    var RED = "#ff0000";
    var personMarkers = new OverlaysCache();
    var summaryPolys = [];

    var getEditLink = function(person) {
        if (person.name == '') {
            return '';
        }
        return '<a class="ui-icon ui-icon-gear" href="javascript:Persons.edit(' + person.id + ')"></a>';
    };

    var getDeleteLink = function(row, person) {
        if (person.name == '') {
            return '';
        }
        return '<a class="ui-icon ui-icon-trash" href="javascript:Persons.remove(' + (row + 1) + ',' + person.id + ')"></a>';
    };

    var getMapLink = function(person) {
        if (person.name == '') {
            var poly = polyFromJson(person.bbox);
            var bounds = poly.getBounds();
            var ne = bounds.getNorthEast();
            var sw = bounds.getSouthWest();
            return '<a class="ui-icon ui-icon-image" href="javascript:Persons.gotoBBox(' + ne.lat() + ',' + ne.lng() + ',' + sw.lat() + ',' + sw.lng() + ')"></a>';
        }
        return '<a class="ui-icon ui-icon-image" href="javascript:Persons.gotoLatLng(' + person.lat + ',' + person.lon + ')"></a>';
    };

    var getEmailLink = function(person) {
        if (person.name == '') {
            return '<a href="javascript:Users.showSendMailForm(' + person.createdById + ')"><span class="ui-icon ui-icon-mail-closed"></span></a>';
        }
        return '';
    };

    var createDownloadLink = function(searchRequestData) {
        var url = "searchDownload.do?fromDate=" + escape(searchRequestData.fromDate) + "&toDate="
                + escape(searchRequestData.toDate) + "&defects=" + escape(searchRequestData.defects)
                + "&onlyCurrentUser=" + escape(searchRequestData.onlyCurrentUser);
        return '<a href="' + url + '">Download</a>';
    }

    var addToMap = function(row, person, latLngs) {
        if (map) {
            if (person.name == '') {
                var poly = polyFromJson(person.bbox, RED);
                $.each(person.bbox, function(i, vertex)
                {
                    var latLng = new GLatLng(vertex.lat, vertex.lon);
                    latLngs.push(latLng);
                });
                map.addOverlay(poly);
            } else {
                var latLng = new GLatLng(person.lat, person.lon);
                latLngs.push(latLng);
                var marker = new GMarker(latLng);
                personMarkers.add(person.id, marker);
                map.addOverlay(marker);
            }
        }
    };

    var showDefectData = function(bounds, tensCode, unitsCode) {
        var success = function(defectDataList, textStatus) {
            var extentString =
                    '[' + Math.round(bounds.getSouthWest().lat()) + " , "
                            + Math.round(bounds.getSouthWest().lng()) + '] ' +
                    '[' + Math.round(bounds.getNorthEast().lat()) + " , "
                            + Math.round(bounds.getNorthEast().lng()) + '] '
            var title = 'Summary of Birth Defects in ' + extentString;
            $('#search_results').html(Persons.createResultsTable(defectDataList, '', title));
        };
        var error = function() {
            alert("Error occurred getting defect data for " + tensCode);
        }
        doAjax("GET", "defectsSummary.do", ({"tensCode" : tensCode, "unitsCode" : unitsCode}), success, error);
    };

    var addSummariesToMap = function (url) {
        var success = function(data, textStatus) {
            $.each(summaryPolys, function(i, item) {
                if (map) {
                    map.removeOverlay(item);
                }
                summaryPolys = [];
            })
            var summariesList = data;
            $.each(summariesList, function(i, item) {
                if (map) {
                    var poly = polyFromJson(item.vertices, RED);
                    map.addOverlay(poly);
                    GEvent.addListener(poly, "click", function() {
                        showDefectData(poly.getBounds(), item.tensCode, item.unitsCode)
                    });
                    summaryPolys.push(poly);
                }
            });
            hideLoadingMessage();
        };
        var error = function(XMLHttpRequest, textStatus, errorThrown) {
            hideLoadingMessage();
            alert("Error getting summaries");
        };
        showLoadingMessage();
        doAjax("GET", url, "", success, error);
    };

    return {

        gotoBBox : function(neLat, neLng, swLat, swLng) {
            var poly = polyFromBounds(neLat, neLng, swLat, swLng, RED);
            var bounds = poly.getBounds();
            var zoomlevel = map.getBoundsZoomLevel(bounds);
            if (zoomlevel > DETAIL_ZOOM_LEVEL) {
                zoomlevel = DETAIL_ZOOM_LEVEL;
            }
            map.setCenter(bounds.getCenter(), zoomlevel)
            map.addOverlay(poly);
        },


        gotoLatLng : function (lat, lng) {
            if (map) {
                map.setCenter(new GLatLng(lat, lng), DETAIL_ZOOM_LEVEL);
            }
        },

        createResultsTable : function(personsOrHazards, downloadLink, title) {
            if (personsOrHazards.length == 0) {
                return "<p>No results found</p>";
            }
            if (map) {
                map.clearOverlays();
            }
            personMarkers = new OverlaysCache();
            var latLngs = [];
            var title = '<p>' + title + ' ' + downloadLink + '</p>';
            html = '<div id="">';
            $.each(personsOrHazards, function(i, item) {
                if (item.dateOfBirth) { // must be person
                    html += item.name + '(' + item.dateOfBirth + ')';
                    html += '<div>' + item.birthDefects;
                    html += getMapLink(item);
                    html += getEmailLink(item);
                    html += getEditLink(item);
                    html += getDeleteLink(i, item);
                    html += '</div>';
                    addToMap(i, item, latLngs);
                } else { // must be hazard
                    html += '<div>';
                    html += item.name;
                    html += '<div>' + item.NAICTitle + '</div>';
                    html += '<div>' + truncateIfTooLong(item.description) + '</div>';
                    html += EnvHazards.getMapLink(item);
                    html += EnvHazards.getDeleteLink(i, item);
                    html += '</div>';
                    EnvHazards.addHazardToMap(item, latLngs);
                }
            });
            if (map) {
                var bounds = boundsFromLatLngs(latLngs);
                var zoomlevel = map.getBoundsZoomLevel(bounds);
                if (zoomlevel > DETAIL_ZOOM_LEVEL) {
                    zoomlevel = DETAIL_ZOOM_LEVEL;
                }
                if (zoomlevel < INITIAL_ZOOM_LEVEL) {
                    zoomlevel = INITIAL_ZOOM_LEVEL;
                }
                map.setCenter(bounds.getCenter(), zoomlevel)
            }
            html += '</div>';
            return html;
        },

        edit : function(id) {
            clearAllMessages();
            var data = ({ "id": id });
            var success = function(data, textStatus) {
                var person = data;
                showPersonForm();
                $("input[name='person_id']").val(person.id);
                $("input[name='person_name']").val(person.name);
                $("input[name='person_lat']").val(person.lat);
                $("input[name='person_lon']").val(person.lon);
                $("input[name='person_dateOfBirth']").val(person.dateOfBirth);
                var selectElement = $("select[name='person_defects']");
                $.each(person.birthDefects, function(i, defectName) {
                    $.each(selectElement.children(), function(i, option) {
                        if (defectName == option.value) {
                            option.selected = true;
                        }
                    });
                });
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Error retrieving person");
            };
            doAjax("GET", "person.do", data, success, error);
        },

        save : function() {
            clearAllMessages();
            showLoadingMessage();
            var data = ({
                "id" : $("input[name='person_id']").val(),
                "name" : $("input[name='person_name']").val(),
                "lat" : $("input[name='person_lat']").val(),
                "lon" : $("input[name='person_lon']").val(),
                "dateOfBirth" : $("input[name='person_dateOfBirth']").val(),
                "defects" : $("select[name='person_defects']").val().join(",")
            });
            var success = function(data, textStatus) {
                lat = $("input[name='person_lat']").val();
                lon = $("input[name='person_lon']").val();
                $("input[name='person_id']").val('');
                $("input[name='person_name']").val('');
                $("input[name='person_lat']").val('');
                $("input[name='person_lon']").val('');
                $("input[name='person_dateOfBirth']").val('');
                $('#add_person_errors').append("<font color='green'>Saved.</font><br/>");
                if (map) {
                    map.addOverlay(new GMarker(new GLatLng(lat, lon)));
                }
                hideLoadingMessage();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                showError(XMLHttpRequest.responseText, $('#add_person_errors'))
            };
            doAjax("POST", "savePerson.do", data, success, error);
        },

        search : function() {
            $('#search_results').html('');
            clearAllMessages();
            showLoadingMessage();
            var postData = ({
                "fromDate" : $("input[id='search_fromdate']").val(),
                "toDate" : $("input[id='search_todate']").val(),
                "defects" : $("select[id='search_defects']").val(),
                "onlyCurrentUser" : $("input[id='search_onlyLoggedInUser']").attr('checked')
            });
            var success = function(data, textStatus) {
                var downloadLink = createDownloadLink(postData);
                $('#search_results').html(Persons.createResultsTable(data, downloadLink, "Search Results"));
                hideLoadingMessage();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                hideLoadingMessage();
                $('#search_errors').append("<font color='red'>Error occurred.</font><br/>");
            };
            doAjax("GET", "search.do", postData, success, error);
        },

        remove: function(row, id) {
            clearAllMessages();
            showLoadingMessage();
            var data = ({ "id": id });
            var success = function(data, textStatus) {
                var table = document.getElementById("searchResultsTable");
                table.deleteRow(row);
                if (map) {
                    personMarkers.clearFromMap(id, map)
                }
                hideLoadingMessage();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Error deleting person");
            };
            doAjax("POST", "deletePerson.do", data, success, error);
        },

        addTenByTenSummariesToMap : function () {
            addSummariesToMap("personTenByTenSummaries.do");
        },

        addOneByOneSummariesToMap : function () {
            addSummariesToMap("personOneByOneSummaries.do");
        },

        showSummaries : function(zoomLevel) {
            if (zoomLevel > ONE_BY_ONE_SUMMARY_ZOOM_LEVEL) {
                Persons.addOneByOneSummariesToMap();
            }
            else {
                Persons.addTenByTenSummariesToMap();
            }
        }

    }

}
        ();


