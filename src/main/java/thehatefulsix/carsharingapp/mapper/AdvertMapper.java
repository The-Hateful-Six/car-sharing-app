package thehatefulsix.carsharingapp.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import thehatefulsix.carsharingapp.dto.ads.AdvertDto;
import thehatefulsix.carsharingapp.dto.ads.CreateAdvertRequestDto;
import thehatefulsix.carsharingapp.model.advert.Advert;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface AdvertMapper {
    AdvertDto toDto(Advert advert);

    Advert toAds(CreateAdvertRequestDto createAdvertRequestDto);

    void updateAdvert(CreateAdvertRequestDto updatedRequestDto,
                      @MappingTarget Advert advertToUpdate);
}
