package uos.capstone.dms.domain.board;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostRegisterDTO {

    private String title;
    private String content;
    private List<MultipartFile> images;
}
