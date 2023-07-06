package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberDataDTO;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberDTO;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberDTO memberToMemberDTO(Member member);

    @Mapping(target = "password", ignore = true)
    Member memberDTOToMember(MemberDTO memberDTO);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "uuid", source = "member.memberImage.uuid")
    MemberDataDTO memberToMemberDataDTO(Member member);

    @Mapping(target = "social", ignore = true)
    @Mapping(target = "provider", ignore = true)
    Member memberRequestDTOToMember(MemberRequestDTO memberRequestDTO);

    @Mapping(target = "social", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "memberImage", ignore = true)
    MemberDTO requestDTOToMemberDTO(MemberRequestDTO memberRequestDTO);
}
