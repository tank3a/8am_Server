package uos.capstone.dms.domain.pet;


import lombok.*;
import uos.capstone.dms.domain.user.Member;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetId implements Serializable {

    private Member member;
    private String petId;
}
