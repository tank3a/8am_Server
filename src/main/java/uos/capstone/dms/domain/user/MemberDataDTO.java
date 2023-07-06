package uos.capstone.dms.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uos.capstone.dms.domain.auth.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberDataDTO {

    private String userId;
    private String username;
    private String nickname;
    private int gender;
    private LocalDate birth;
    private String email;
    private String phoneNo;
    private String zipcode;
    private boolean social;
    private Provider provider;
    private String street;
    private String addressDetail;
    private String uuid;
    private List<Role> roles;
    private LocalDate createdDate;
}
