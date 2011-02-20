Bdtui.hazardsPage = SC.Page.create({

   addView: SC.ScrollView.design({
        contentView: SC.View.design({
            layout: { top:0, left: 0, right:0, bottom: 0 },
            childViews: [
                SC.LabelView.design({
                    layout: { centerY: 0, top:0, height: 10, left: 5, width: 50 },
                    controlSize: SC.SMALL_CONTROL_SIZE,
                    fontWeight: SC.BOLD_WEIGHT,
                    value: 'Name:'
                }),
                SC.TextFieldView.design({
                    layout: { top:0, height: 10, left: 35, width: 180 }
                })
            ]
        })
   })
});