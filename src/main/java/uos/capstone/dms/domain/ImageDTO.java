package uos.capstone.dms.domain;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ImageDTO {

    private String uuid;
    private String fileName;
    private String fileUrl;
}
