/* adapted from https://github.com/hughsk/png-chunks-extract (MIT) and https://github.com/SheetJS/js-crc32 (Apache 2.0) (C) 2014-present SheetJS -- http://sheetjs.com https://github.com/alexhorn/uint8array-base64 (Public Domain) */
window.rewrite_pHYs_chunk = function(data, ppmx, ppmy, base64) {


    // https://github.com/alexhorn/uint8array-base64
    const base64chars = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'];
    //const base64inv = {'0':52,'1':53,'2':54,'3':55,'4':56,'5':57,'6':58,'7':59,'8':60,'9':61,'A':0,'B':1,'C':2,'D':3,'E':4,'F':5,'G':6,'H':7,'I':8,'J':9,'K':10,'L':11,'M':12,'N':13,'O':14,'P':15,'Q':16,'R':17,'S':18,'T':19,'U':20,'V':21,'W':22,'X':23,'Y':24,'Z':25,'a':26,'b':27,'c':28,'d':29,'e':30,'f':31,'g':32,'h':33,'i':34,'j':35,'k':36,'l':37,'m':38,'n':39,'o':40,'p':41,'q':42,'r':43,'s':44,'t':45,'u':46,'v':47,'w':48,'x':49,'y':50,'z':51,'+':62,'/':63};

    function encodeBase64(buf) {
        let str = new Array(Math.ceil(buf.length * 4 / 3));
        for (let i = 0; i < buf.length; i += 3) {
            const b0 = buf[i],
                b1 = buf[i + 1],
                b2 = buf[i + 2],
                b3 = buf[i + 3];
            str[i * 4 / 3] = base64chars[b0 >>> 2];
            str[i * 4 / 3 + 1] = base64chars[b0 << 4 & 63 | (b1 || 0) >>> 4];
            if (i + 1 < buf.length) {
                str[i * 4 / 3 + 2] = base64chars[b1 << 2 & 63 | (b2 || 0) >>> 6];
                if (i + 2 < buf.length) {
                    str[i * 4 / 3 + 3] = base64chars[b2 & 63];
                } else {
                    return str.join('') + '=';
                }
            } else {
                return str.join('') + '==';
            }
        }
        return str.join('');
    }
    /*
    function decodeBase64 (str) {
      let pad = 0;
      for (let i = str.length - 1; i >= 0; --i) {
        if (str.charAt(i) == '=') {
          ++pad;
        } else {
          break;
        }
      }
      let buf = new Uint8Array(str.length * 3 / 4 - pad);
      for (let i = 0; i < str.length - pad; i += 4) {
        const c0 = base64inv[str.charAt(i)], c1 = base64inv[str.charAt(i + 1)], c2 = base64inv[str.charAt(i + 2)], c3 = base64inv[str.charAt(i + 3)];
        buf[i * 3 / 4] = c0 << 2 & 255 | c1 >>> 4;
        if (i + 2 < str.length - pad) {
          buf[i * 3 / 4 + 1] = c1 << 4 & 255 | c2 >>> 2;
          if (i + 3 < str.length - pad) {
            buf[i * 3 / 4 + 2] = c2 << 6 & 255 | c3;
          }
        }
      }
      return buf;
    }*/

    /* crc32.js (C) 2014-present SheetJS -- http://sheetjs.com */
    /* vim: set ts=2: */
    /*exported CRC32 */
    var CRC32;
    (function(factory) {
        /*jshint ignore:start */
        if (typeof DO_NOT_EXPORT_CRC === 'undefined') {
            if ('object' === typeof exports) {
                factory(exports);
            } else if ('function' === typeof define && define.amd) {
                define(function() {
                    var module = {};
                    factory(module);
                    return module;
                });
            } else {
                factory(CRC32 = {});
            }
        } else {
            factory(CRC32 = {});
        }
        /*jshint ignore:end */
    }(function(CRC32) {
        CRC32.version = '1.1.1';
        /* see perf/crc32table.js */
        /*global Int32Array */
        function signed_crc_table() {
            var c = 0,
                table = new Array(256);

            for (var n = 0; n != 256; ++n) {
                c = n;
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                c = ((c & 1) ? (-306674912 ^ (c >>> 1)) : (c >>> 1));
                table[n] = c;
            }

            return typeof Int32Array !== 'undefined' ? new Int32Array(table) : table;
        }

        var T = signed_crc_table();

        function crc32_bstr(bstr, seed) {
            var C = seed ^ -1,
                L = bstr.length - 1;
            for (var i = 0; i < L;) {
                C = (C >>> 8) ^ T[(C ^ bstr.charCodeAt(i++)) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ bstr.charCodeAt(i++)) & 0xFF];
            }
            if (i === L) C = (C >>> 8) ^ T[(C ^ bstr.charCodeAt(i)) & 0xFF];
            return C ^ -1;
        }

        function crc32_buf(buf, seed) {
            if (buf.length > 10000) return crc32_buf_8(buf, seed);
            var C = seed ^ -1,
                L = buf.length - 3;
            for (var i = 0; i < L;) {
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
            }
            while (i < L + 3) C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
            return C ^ -1;
        }

        function crc32_buf_8(buf, seed) {
            var C = seed ^ -1,
                L = buf.length - 7;
            for (var i = 0; i < L;) {
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
                C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
            }
            while (i < L + 7) C = (C >>> 8) ^ T[(C ^ buf[i++]) & 0xFF];
            return C ^ -1;
        }

        function crc32_str(str, seed) {
            var C = seed ^ -1;
            for (var i = 0, L = str.length, c, d; i < L;) {
                c = str.charCodeAt(i++);
                if (c < 0x80) {
                    C = (C >>> 8) ^ T[(C ^ c) & 0xFF];
                } else if (c < 0x800) {
                    C = (C >>> 8) ^ T[(C ^ (192 | ((c >> 6) & 31))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | (c & 63))) & 0xFF];
                } else if (c >= 0xD800 && c < 0xE000) {
                    c = (c & 1023) + 64;
                    d = str.charCodeAt(i++) & 1023;
                    C = (C >>> 8) ^ T[(C ^ (240 | ((c >> 8) & 7))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | ((c >> 2) & 63))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | ((d >> 6) & 15) | ((c & 3) << 4))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | (d & 63))) & 0xFF];
                } else {
                    C = (C >>> 8) ^ T[(C ^ (224 | ((c >> 12) & 15))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | ((c >> 6) & 63))) & 0xFF];
                    C = (C >>> 8) ^ T[(C ^ (128 | (c & 63))) & 0xFF];
                }
            }
            return C ^ -1;
        }
        CRC32.table = T;
        CRC32.bstr = crc32_bstr;
        CRC32.buf = crc32_buf;
        CRC32.str = crc32_str;
    }));

    // Used for fast-ish conversion between uint8s and uint32s/int32s.
    // Also required in order to remain agnostic for both Node Buffers and
    // Uint8Arrays.
    var uint8 = new Uint8Array(4);
    var int32 = new Int32Array(uint8.buffer);
    var uint32 = new Uint32Array(uint8.buffer);

    var pHYsFound = false;

    if (data[0] !== 0x89 || data[1] !== 0x50 || data[2] !== 0x4E || data[3] !== 0x47 || data[4] !== 0x0D || data[5] !== 0x0A || data[6] !== 0x1A || data[7] !== 0x0A) {
        throw new Error('Invalid .png file header: possibly caused by DOS-Unix line ending conversion?');
    }

    var ended = false
    var idx = 8

    while (idx < data.length) {
        // Read the length of the current chunk,
        // which is stored as a Uint32.
        uint8[3] = data[idx++]
        uint8[2] = data[idx++]
        uint8[1] = data[idx++]
        uint8[0] = data[idx++]

        // Chunk includes name/type for CRC check (see below).
        var length = uint32[0] + 4
        var chunk = new Uint8Array(length)
        chunk[0] = data[idx++]
        chunk[1] = data[idx++]
        chunk[2] = data[idx++]
        chunk[3] = data[idx++]

        // Get the name in ASCII for identification.
        var name = (
            String.fromCharCode(chunk[0]) +
            String.fromCharCode(chunk[1]) +
            String.fromCharCode(chunk[2]) +
            String.fromCharCode(chunk[3])
        );

        console.log("chunk found " + name + ", length = " + (length - 4));


        var chunkDataStart = idx;

        // Read the contents of the chunk out of the main buffer.
        for (var i = 4; i < length; i++) {
            chunk[i] = data[idx++];
        }

        var crcStart = idx;
        // Read out the CRC value for comparison.
        // It's stored as an Int32.
        uint8[3] = data[idx++];
        uint8[2] = data[idx++];
        uint8[1] = data[idx++];
        uint8[0] = data[idx++];

        var crcActual = int32[0];
        var crcExpect = CRC32.buf(chunk);
        if (crcExpect !== crcActual) {
            throw new Error(
                'CRC values for ' + name + ' header do not match, PNG file is likely corrupted'
            )
        } else {
            console.log("CRCs match! " + crcExpect + " " + crcActual + " " + chunk.length);
        }

        if (name == "IDAT") {

            chunkDataStart = chunkDataStart - 8;

            var len = data.length;

            // create new array with pHYs chunk inserted
            // 4+4+13
            var data2 = new Uint8Array(len + 21);

            // copy before IEND
            for (var i = 0; i < chunkDataStart; i++) {
                data2[i] = data[i];
            }
            // copy IEND to end
            for (var i = chunkDataStart; i < len; i++) {
                data2[i + 21] = data[i];
            }

            var phys = new Uint8Array(13);
            var i = 0;

            // length of pHYs chunk
            int32[0] = 9;
            data2[chunkDataStart++] = uint8[3];
            data2[chunkDataStart++] = uint8[2];
            data2[chunkDataStart++] = uint8[1];
            data2[chunkDataStart++] = uint8[0];

            // pHYs (chunk name)
            phys[i++] = data2[chunkDataStart++] = 'p'.charCodeAt(0);
            phys[i++] = data2[chunkDataStart++] = 'H'.charCodeAt(0);
            phys[i++] = data2[chunkDataStart++] = 'Y'.charCodeAt(0);
            phys[i++] = data2[chunkDataStart++] = 's'.charCodeAt(0);


            // x
            uint32[0] = ppmx;
            phys[i++] = data2[chunkDataStart++] = uint8[3];
            phys[i++] = data2[chunkDataStart++] = uint8[2];
            phys[i++] = data2[chunkDataStart++] = uint8[1];
            phys[i++] = data2[chunkDataStart++] = uint8[0];
            // y 
            uint32[0] = ppmy;
            phys[i++] = data2[chunkDataStart++] = uint8[3];
            phys[i++] = data2[chunkDataStart++] = uint8[2];
            phys[i++] = data2[chunkDataStart++] = uint8[1];
            phys[i++] = data2[chunkDataStart++] = uint8[0];
            // unit = meters
            phys[i++] = data2[chunkDataStart++] = 1;

            var physCRC = CRC32.buf(phys);
            int32[0] = physCRC;

            data2[chunkDataStart++] = uint8[3];
            data2[chunkDataStart++] = uint8[2];
            data2[chunkDataStart++] = uint8[1];
            data2[chunkDataStart++] = uint8[0];

            if (base64) {
                return encodeBase64(data2);
            }
            return data2;


        }

        if (name == "pHYs") {
            console.log("pHYs chunk found, rewriting!!!!!!!!!!!!!");

            uint8[3] = data[chunkDataStart];
            uint8[2] = data[chunkDataStart + 1];
            uint8[1] = data[chunkDataStart + 2];
            uint8[0] = data[chunkDataStart + 3];
            console.log("pixels per metre x =" + uint32[0]);

            uint8[3] = data[chunkDataStart + 4];
            uint8[2] = data[chunkDataStart + 5];
            uint8[1] = data[chunkDataStart + 6];
            uint8[0] = data[chunkDataStart + 7];
            console.log("pixels per metre y =" + uint32[0]);
            console.log("unit (1=m) =" + data[chunkDataStart + 8]);

            var phys = new Uint8Array(13);
            var i = 0;


            // pHYs (chunk name)
            phys[i++] = 'p'.charCodeAt(0);
            phys[i++] = 'H'.charCodeAt(0);
            phys[i++] = 'Y'.charCodeAt(0);
            phys[i++] = 's'.charCodeAt(0);
            // x
            uint32[0] = ppmx;
            phys[i++] = data[chunkDataStart++] = uint8[3];
            phys[i++] = data[chunkDataStart++] = uint8[2];
            phys[i++] = data[chunkDataStart++] = uint8[1];
            phys[i++] = data[chunkDataStart++] = uint8[0];
            // y 
            uint32[0] = ppmy;
            phys[i++] = data[chunkDataStart++] = uint8[3];
            phys[i++] = data[chunkDataStart++] = uint8[2];
            phys[i++] = data[chunkDataStart++] = uint8[1];
            phys[i++] = data[chunkDataStart++] = uint8[0];
            // unit = meters
            phys[i++] = data[chunkDataStart++] = 1;

            var physCRC = CRC32.buf(phys);
            int32[0] = physCRC;

            data[crcStart++] = uint8[3];
            data[crcStart++] = uint8[2];
            data[crcStart++] = uint8[1];
            data[crcStart++] = uint8[0];

            if (base64) {
                return encodeBase64(data);
            }
            return data;
        }

    }

    throw new Error('.png file ended prematurely: no IEND or pHYs header was found');

}