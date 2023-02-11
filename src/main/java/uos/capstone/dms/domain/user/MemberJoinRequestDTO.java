package uos.capstone.dms.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequestDTO {

    private String userId;
    private String username;
    private String password;
    private String nickname;
    private int gender;
    private LocalDate birth;
    private String email;
    private String phoneNo;
    private int zipcode;
    private String street;
    private String addressDetail;
    private MultipartFile memberImage;
}
