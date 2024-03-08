package com.bitharmony.comma.album.album.util;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.album.album.dto.AlbumResponse;
import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.file.util.NcpImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumConvertUtil {

    private final NcpImageUtil ncpImageUtil;

    public AlbumListResponse convertToDto(Album album) {
        return AlbumListResponse.builder()
                .id(album.getId())
                .albumname(album.getAlbumname())
                .genre(album.getGenre())
                .fileUrl(album.getFilePath()) // TODO: file URL로 변경하기 + NCP MusicUtil과 설정 통합
                .imgUrl(convertAlbumImageUrl(album.getImagePath()))
                .artistUsername(album.getMember().getUsername())
                .artistNickname(album.getMember().getNickname())
                .build();
    }

    public AlbumResponse albumToResponseDto(Album album) {
        return AlbumResponse.builder()
                .id(album.getId())
                .albumname(album.getAlbumname())
                .genre(album.getGenre())
                .license(album.isLicense())
                .licenseDescription(album.getLicenseDescription())
                .imgUrl(convertAlbumImageUrl(album.getImagePath()))
                .fileUrl(album.getFilePath())
                .permit(album.isPermit())
                .price(album.getPrice())
                .artistNickname(album.getMember().getNickname())
                .artistUsername(album.getMember().getUsername())
                .build();
    }

    private String convertAlbumImageUrl(String imagePath) {
        return ncpImageUtil.getAlbumImageUrl(imagePath);
    }
}
