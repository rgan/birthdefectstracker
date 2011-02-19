var BirthDefects = function() {
    var createSelectOption = function (name, isSelected) {
        // only thing that works in IE
        var option = document.createElement('option');
        option.value = name;
        option.selected = isSelected;
        option.appendChild(document.createTextNode(name));
        return option;
    };

    var populateBirthDefectsDropdown = function (defectsList, selectElement, selectedName)
    {
        selectElement.children().remove().end();
        // add "All" element to search dropdown
        if (selectElement.attr('name') == 'search_defects') {
            selectElement.append(createSelectOption("All", true));
        }
        $.each(defectsList, function(i, item) {
            var isSelected = selectedName == item.name;
            selectElement.append(createSelectOption(item.name, isSelected));
            //selectElement.append(new Option(item.name, item.name, false, isSelected));
        });
    };
    
    return {

        add : function () {
            clearAllMessages();
            var name = $("input[name='birth_defect_name']").val();
            $.ajax({
                type: "POST",
                url: "addBirthDefect.do",
                timeout: 10000,
                data: ({
                    "name" : name,
                    "code" : $("input[name='birth_defect_code']").val()
                }),
                success: function(data, textStatus) {
                    $("input[name='birth_defect_name']").val('');
                    $("input[name='birth_defect_code']").val('');
                    $('#addBirthDefectForm').hide();
                    BirthDefects.populateDropdowns([$("select[name='person_defects']"), $("select[name='search_defects']")], name);
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    showError(XMLHttpRequest.responseText, $('#add_birth_defect_errors'))
                }
            });
        },

        populateDropdowns : function (selectElements, selectedName) {
            showLoadingMessage();
            var success = function(data, textStatus) {
                var defectsList = data; //jsonParse(data);
                $.each(selectElements, function(i, item) {
                    populateBirthDefectsDropdown(defectsList, item, selectedName);
                });
                hideLoadingMessage();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                hideLoadingMessage();
                //alert("Error getting all birth defects");
                alert(XMLHttpRequest.responseText);
            };
            doAjax("GET", "allBirthDefects.do", "", success, error);
        },


        showAddDefect : function () {
            $('#addBirthDefectForm').show();
        }
    }
}();
