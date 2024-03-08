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
                .imgPath(ncpImageUtil.getAlbumImageUrl(album.getImagePath()))
                .permit(album.isPermit())
                .price(album.getPrice())
                .artistUsername(album.getMember().getUsername())
                .artistNickname(album.getMember().getNickname())
                .build();
    }

    public AlbumResponse albumToResponseDto(Album album) {
        album = album.toBuilder()
                //.filePath(albumService.getAlbumFileUrl(album.getFilePath()))
                .imagePath(ncpImageUtil.getAlbumImageUrl(album.getImagePath()))
                .build();

        return AlbumResponse.builder()
                .id(album.getId())
                .albumname(album.getAlbumname())
                .genre(album.getGenre())
                .license(album.isLicense())
                .licenseDescription(album.getLicenseDescription())
                .imgPath(album.getImagePath())
                .filePath(album.getFilePath())
                .permit(album.isPermit())
                .price(album.getPrice())
                .artistNickname(album.getMember().getNickname())
                .artistUsername(album.getMember().getUsername())
                .build();
    }
}
