package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.tools.UnsupportedPointcutPrimitiveException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; // 검색의 종류 t, c, w, tc, tw, twc

    private String keyword;

    public  String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String...props){
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }

    private String link;

    public String getLink(){

        if(link == null){
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("page=" + this.page);

            stringBuilder.append("&size" + this.size);

            if(type != null && type.length() > 0){
                stringBuilder.append("&type" + type);
            }

            if (keyword != null){
                try {
                    stringBuilder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                }catch (UnsupportedEncodingException e) {
                }
            }
            link = stringBuilder.toString();
        }
        return  link;
    }
}
