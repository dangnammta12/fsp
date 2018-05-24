(function() {
        $.extend = function() {
        var target = arguments[0] || {}, length = arguments.length, deep = false, i = 1, src, value;
        if (typeof target === "boolean") {
            deep = target;
            target = arguments[1] || {};
            i = 2;
        }
        for (; i < length; i++) {
            for (var key in arguments[i]) {
                src = target[key];
                value = arguments[i][key];
                if (src === value) {
                    continue;
                }
                if (deep && value) {
                    var clone = $.isArray(src) ? [] : {};
                    $.extend(deep, clone, value);
                } else {
                    if (value !== undefined) {
                        target[key] = value;
                    }
                }
            }
        }
        return target;
    };
    var dtIds = {},
            dtTimer = 0,
            onoff = true,
            dtInterval = 60000;
    function fill2(value) {
        return value < 10 ? ('0' + value) : value;
    }
    function format(t) {
        t = zm.intval(t);
        if (t <= 0)
            return '';
        var e = [[11, 'sĂ¡ng'], [14, 'trÆ°a'], [19, 'chiá»�u']],
                f = ['Chá»§ Nháº­t', 'Thá»© Hai', 'Thá»© Ba', 'Thá»© TÆ°', 'Thá»© NÄƒm', 'Thá»© SĂ¡u', 'Thá»© Báº£y'],
                g = new Date(),
                j = new Date(t * 1000),
                d = Math.floor(g.getTime() / 1000) - t;
        if (d < 60)
            return (d < 0 ? 0 : d).toString() + ' giĂ¢y trÆ°á»›c';
        if (d < 3600)
            return Math.floor(d / 60) + ' phĂºt trÆ°á»›c';
        if (d < 43200)
            return Math.floor(d / 3600) + ' tiáº¿ng trÆ°á»›c';
        var h = j.getHours(),
                m = fill2(j.getMinutes());
        if (d < 518400) {
            var b = 'tá»‘i';
            for (var i = 0; i < 3; i++)
                if (h < e[i][0]) {
                    b = e[i][1];
                    break;
                }
            d = (g.getDay() + 7 - j.getDay()) % 7;
            var k = '';
            if (d == 0)
                k = 'hĂ´m nay';
            else if (d == 1)
                k = 'hĂ´m qua';
            else
                k = f[j.getDay()];
            return fill2(h <= 12 ? h : h % 12).toString() + ':' + m + ' ' + b + ' ' + k;
        }
        h = fill2(h);
        return h + ':' + m + ' ' + fill2(j.getDate()) + '/' + fill2(j.getMonth() + 1) + '/' + j.getFullYear();
    }
    function renderTime(id) {
        var z = zm('#' + id),
                ts = parseInt(z.attr('rel'));
        z.html(format(ts));
        return ts;
    }
    function timer() {
        var current = Math.round((new Date()).getTime() / 1000);
        for (var id in dtIds) {
            if (dtIds[id] <= current) {
                var ts = renderTime(id);
                if (current - ts < 3600)
                    dtIds[id] = current + 60;
                else if (current - ts < 86400)
                    dtIds[id] = current + 3600;
                else
                    dtIds[id] = Number.MAX_VALUE;
            }
        }
        if (onoff)
            dtTimer = setTimeout(timer, dtInterval);
    }
    window.zmDateTime = {
        format: format,
        add: function(e, force) {
            if (zm.isArray(e)) {
                for (var i = 0, t; t = e[i]; i++)
                    if (force || dtIds[t] == undefined) {
                        dtIds[t] = 0;
                        renderTime(t);
                    }
            }
            else if (dtIds[e] == undefined) {
                dtIds[e] = 0;
                renderTime(e);
            }
        },
        setOnOff: function(val) {
            onoff = val;
            if (!val && dtTimer != 0) {
                clearTimeout(dtTimer);
                dtTimer = 0;
            }
        },
        setInterval: function(interval) {
            dtInterval = interval;
        }
    };
    setTimeout(timer, dtInterval);
})();
(function() {
    function _render(tpl, data) {
        for (var f in data)
            tpl = tpl.replace(new RegExp("{" + f + "}", "g"), data[f]);
        tpl = tpl.replace(/{[a-z_0-9]+}/gi, "");
        return tpl;
    }
    window.zmTemplate = {
        render: function(template, data) {
            if (zm.isArray(data)) {
                var result = new Array();
                for (var i = 0; i < data.length; i++)
                    result.push(_render(template, data));
                return result;
            }
            else
                return _render(template, data);
        }
    };
})();
(function() {
    window.zmString = {
        formatNumber: function(number, separator) {
            try {
                number = parseFloat(number);
            } catch (e) {
            }
            if (isNaN(number))
                return '';
            number = number.toString();
            separator = separator || '.';
            var result = new Array(),
                    t = number.split('.'),
                    fractional = '';
            if (t && t[1]) {
                fractional = (separator == '.' ? ',' : '.') + t[1];
                number = t[0];
            }
            for (var k = number.length; k > 0; k -= 3)
                result.push(number.substring(k - 3, k));
            return result.reverse().join(separator) + fractional;
        },
        capitalize: function(str, sep) {
            if (typeof str != 'string')
                return null;
            var ar = str.split(sep);
            if (ar.length == 0)
                return str;
            for (var i = 0; i < ar.length; i++)
                if (ar[i])
                    ar[i] = ar[i].charAt(0).toUpperCase() + ar[i].substr(1);
            return ar.join(sep);
        }
    };
})();