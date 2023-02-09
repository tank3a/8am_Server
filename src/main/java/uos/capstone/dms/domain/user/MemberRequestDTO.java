package uos.capstone.dms.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {

    private String userId;
    private String username;
    private String nickname;
    private int gender;
    private LocalDate birth;
    private String email;
    private String phoneNo;
    private int zipcode;
    private String street;
    private String addressDetail;
    private MemberImage memberImage;
}
