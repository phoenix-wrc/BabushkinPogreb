package org.example.utils;

import org.hashids.Hashids;

public class CryptoTool {
    private final Hashids hashids;
    int minHashLength;

    public CryptoTool(String salt) {
        minHashLength = 10;
        hashids = new Hashids(salt, minHashLength);
    }

    public String hashOf(Long id) {
        return hashids.encode(id);
    }

    public Long idOf(String id) {
        long[] res = hashids.decode(id);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
