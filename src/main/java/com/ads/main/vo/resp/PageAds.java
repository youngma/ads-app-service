package com.ads.main.vo.resp;


import lombok.Getter;

import java.util.List;

public record PageAds<T>(List<T> content, long totalPages, long totalElements, long size) {

}
