// ==========================================================================
// Project:   Bdtui - mainPage
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Bdtui */

// This page describes the main user interface for your application.  
Bdtui.mainPage = SC.Page.design({

    // The main pane is made visible on screen as soon as your app is loaded.
    // Add childViews to this pane for views to display immediately on page
    // load.
    mainPane: SC.MainPane.design({
        childViews: 'toolbarView bodyView'.w(),

        toolbarView: SC.ToolbarView.design({
            layout: { top: 0, left: 0, right: 0, height: 36 },
            childViews: 'labelView loginButton'.w(),
            anchorLocation: SC.ANCHOR_TOP,

            labelView: SC.LabelView.design({
                layout: { centerY: 0, height: 24, left: 8, width: 200 },
                controlSize: SC.LARGE_CONTROL_SIZE,
                fontWeight: SC.BOLD_WEIGHT,
                value:   'Birth Defects Tracker'
            }),

            loginButton: SC.ButtonView.design({
                layout: { centerY: 0, height: 24, right: 12, width: 100 },
                title:  "Login"
            })
        }),

        bodyView: SC.View.design({
            layout: { top: 36, left: 0, right: 0, bottom: 0 },
            childViews: 'tabsView mapView resultView'.w(),
            backgroundColor: 'white',

            tabsView: SC.TabView.design({
                layout: { top: 0, left: 0, width: 250 },
                value: 'main',

                items: [
                    { title: "Add Hazard", value: "Bdtui.hazardsPage.addView" },
                    { title: "Search Hazard", value: "main" }
                ],

                itemTitleKey: 'title',
                itemValueKey: 'value',

                userDefaultKey: "mainPane"

            }),
            
            mapView: SC.ScrollView.design({
                layout: { top: 0, left: 250, width: 600},

                contentView: SC.StaticContentView.design({
                    layout: { width: 600, height: 400 },
//<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAACdRRXK7NXPnq1up2FKcnMBRaocTOPuM9HRuzK4fY92iN2TTwpBT8reePu2gMmF4e0uwWzgdYP_-mLg" type="text/javascript"></script>
//<body onload="load()" onunload="GUnload()">
                    content: '<div id="map" style="width:600px;height:400px"></div>'
                })
            }),

            resultView: SC.ScrollView.design({
                layout: { top: 0, left: 850 },
                backgroundColor: 'green'
            })

        })

    })

});
