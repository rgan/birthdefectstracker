function CacheEntry(id, overlay) {
    this.id = id;
    this.overlays = [];
    this.overlays.push(overlay);
}
;

function OverlaysCache() {
    this.cache = [];
}

OverlaysCache.prototype.add = function(id, overlay) {

    $.each(this.cache, function(i, item) {
        if (item.id == id) {
            item.overlays.push(overlay);
            return;
        }
    });
    this.cache.push(new CacheEntry(id, overlay))
};

OverlaysCache.prototype.clearFromMap = function(id, mapObj)
{
    $.each(this.cache, function(i, item) {
        //alert("Remove:" + item.id + '-' + item.id);
        if (id == item.id) {
            if (mapObj) {
                $.each(item.overlays, function(j, overlay) {
                    mapObj.removeOverlay(overlay);
                });
            }
        }
    });
};
