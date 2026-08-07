package com.example.mypage.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.mypage.entity.AvatarItemEntity;
import com.example.mypage.repository.AvatarItemRepository;

@Component
public class AvatarItemInitializer implements CommandLineRunner {

    @Autowired
    private AvatarItemRepository avatarItemRepository;

    @Override
    public void run(String... args) throws Exception {

        // ★ 初期アバター（Nレア）だけ登録
        //   → ガチャ排出対象外（Nは抽選しない）
        if (avatarItemRepository.count() == 0) {

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("BASE")
                    .name("デフォルト")
                    .cssClass("base-default")
                    .rarity("N")
                    .build()
            );

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("EAR")
                    .name("ふつうの耳")
                    .cssClass("ear-01")
                    .rarity("N")
                    .build()
            );

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("EYE")
                    .name("ふつうの目")
                    .cssClass("eye-01")
                    .rarity("N")
                    .build()
            );

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("FACE")
                    .name("ふつうのほっぺ")
                    .cssClass("face-01")
                    .rarity("N")
                    .build()
            );

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("BODY")
                    .name("ふつうの足")
                    .cssClass("body-01")
                    .rarity("N")
                    .build()
            );

            avatarItemRepository.save(
                AvatarItemEntity.builder()
                    .type("ACCESSORY")
                    .name("ふつうのしっぽ")
                    .cssClass("accessory-01")
                    .rarity("N")
                    .build()
            );
            
         // ▼ Rレア：BASE（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ピンク）").cssClass("base-default-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ベビーピンク）").cssClass("base-default-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（クリーム）").cssClass("base-default-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ベージュ）").cssClass("base-default-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ライトブルー）").cssClass("base-default-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（スカイブルー）").cssClass("base-default-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ラベンダー）").cssClass("base-default-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（ミント）").cssClass("base-default-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（レモン）").cssClass("base-default-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ベース（グレー）").cssClass("base-default-gray").rarity("R").build());

            // ▼ Rレア：EAR（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ピンク）").cssClass("ear-01-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ベビーピンク）").cssClass("ear-01-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（クリーム）").cssClass("ear-01-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ベージュ）").cssClass("ear-01-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ライトブルー）").cssClass("ear-01-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（スカイブルー）").cssClass("ear-01-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ラベンダー）").cssClass("ear-01-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（ミント）").cssClass("ear-01-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（レモン）").cssClass("ear-01-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("耳（グレー）").cssClass("ear-01-gray").rarity("R").build());
         // ▼ Rレア：EYE（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ピンク）").cssClass("eye-01-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ベビーピンク）").cssClass("eye-01-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（クリーム）").cssClass("eye-01-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ベージュ）").cssClass("eye-01-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ライトブルー）").cssClass("eye-01-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（スカイブルー）").cssClass("eye-01-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ラベンダー）").cssClass("eye-01-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（ミント）").cssClass("eye-01-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（レモン）").cssClass("eye-01-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("目（グレー）").cssClass("eye-01-gray").rarity("R").build());

            // ▼ Rレア：FACE（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ピンク）").cssClass("face-01-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ベビーピンク）").cssClass("face-01-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（クリーム）").cssClass("face-01-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ベージュ）").cssClass("face-01-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ライトブルー）").cssClass("face-01-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（スカイブルー）").cssClass("face-01-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ラベンダー）").cssClass("face-01-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（ミント）").cssClass("face-01-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（レモン）").cssClass("face-01-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ほっぺ（グレー）").cssClass("face-01-gray").rarity("R").build());
         // ▼ Rレア：BODY（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ピンク）").cssClass("body-01-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ベビーピンク）").cssClass("body-01-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（クリーム）").cssClass("body-01-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ベージュ）").cssClass("body-01-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ライトブルー）").cssClass("body-01-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（スカイブルー）").cssClass("body-01-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ラベンダー）").cssClass("body-01-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（ミント）").cssClass("body-01-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（レモン）").cssClass("body-01-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("足（グレー）").cssClass("body-01-gray").rarity("R").build());

            // ▼ Rレア：ACCESSORY（10色）
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ピンク）").cssClass("accessory-01-pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ベビーピンク）").cssClass("accessory-01-baby_pink").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（クリーム）").cssClass("accessory-01-cream").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ベージュ）").cssClass("accessory-01-beige").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ライトブルー）").cssClass("accessory-01-light_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（スカイブルー）").cssClass("accessory-01-sky_blue").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ラベンダー）").cssClass("accessory-01-lavender").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（ミント）").cssClass("accessory-01-mint").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（レモン）").cssClass("accessory-01-lemon").rarity("R").build());
            avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("しっぽ（グレー）").cssClass("accessory-01-gray").rarity("R").build());
         // =====================
         // SR DOG（犬）
         // =====================
         avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("犬ベース（SR）").cssClass("base-sr-dog").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("犬耳（SR）").cssClass("ear-sr-dog").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("犬目（SR）").cssClass("eye-sr-dog").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("犬ほっぺ（SR）").cssClass("face-sr-dog").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("犬ボディ（SR）").cssClass("body-sr-dog").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("犬しっぽ（SR）").cssClass("accessory-sr-dog").rarity("SR").build());

         // =====================
         // SR CAT（猫）
         // =====================
         avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("猫ベース（SR）").cssClass("base-sr-cat").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("猫耳（SR）").cssClass("ear-sr-cat").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("猫目（SR）").cssClass("eye-sr-cat").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("猫ほっぺ（SR）").cssClass("face-sr-cat").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("猫ボディ（SR）").cssClass("body-sr-cat").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("猫しっぽ（SR）").cssClass("accessory-sr-cat").rarity("SR").build());

         // =====================
         // SR RABBIT（うさぎ）
         // =====================
         avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("うさぎベース（SR）").cssClass("base-sr-rabbit").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("うさぎ耳（SR）").cssClass("ear-sr-rabbit").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("うさぎ目（SR）").cssClass("eye-sr-rabbit").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("うさぎほっぺ（SR）").cssClass("face-sr-rabbit").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("うさぎボディ（SR）").cssClass("body-sr-rabbit").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("うさぎしっぽ（SR）").cssClass("accessory-sr-rabbit").rarity("SR").build());

         // =====================
         // SR HAMSTER（ハムスター）
         // =====================
         avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("ハムスターベース（SR）").cssClass("base-sr-hamster").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("ハムスター耳（SR）").cssClass("ear-sr-hamster").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("ハムスター目（SR）").cssClass("eye-sr-hamster").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("ハムスターほっぺ（SR）").cssClass("face-sr-hamster").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("ハムスターボディ（SR）").cssClass("body-sr-hamster").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("ハムスターしっぽ（SR）").cssClass("accessory-sr-hamster").rarity("SR").build());

         // =====================
         // SR FOX（キツネ）
         // =====================
         avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("キツネベース（SR）").cssClass("base-sr-fox").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("キツネ耳（SR）").cssClass("ear-sr-fox").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("キツネ目（SR）").cssClass("eye-sr-fox").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("キツネほっぺ（SR）").cssClass("face-sr-fox").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("キツネボディ（SR）").cssClass("body-sr-fox").rarity("SR").build());
         avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("キツネしっぽ（SR）").cssClass("accessory-sr-fox").rarity("SR").build());
      // =====================
      // SSR MYTHIC DOG（神犬）
      // =====================
      avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("神犬ベース（SSR）").cssClass("base-ssr-dog").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("神犬耳（SSR）").cssClass("ear-ssr-dog").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("神犬目（SSR）").cssClass("eye-ssr-dog").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("神犬ほっぺ（SSR）").cssClass("face-ssr-dog").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("神犬ボディ（SSR）").cssClass("body-ssr-dog").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("神犬しっぽ（SSR）").cssClass("accessory-ssr-dog").rarity("SSR").build());

      // =====================
      // SSR MYTHIC CAT（神猫）
      // =====================
      avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("神猫ベース（SSR）").cssClass("base-ssr-cat").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("神猫耳（SSR）").cssClass("ear-ssr-cat").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("神猫目（SSR）").cssClass("eye-ssr-cat").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("神猫ほっぺ（SSR）").cssClass("face-ssr-cat").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("神猫ボディ（SSR）").cssClass("body-ssr-cat").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("神猫しっぽ（SSR）").cssClass("accessory-ssr-cat").rarity("SSR").build());

      // =====================
      // SSR MYTHIC FOX（神狐）
      // =====================
      avatarItemRepository.save(AvatarItemEntity.builder().type("BASE").name("神狐ベース（SSR）").cssClass("base-ssr-fox").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EAR").name("神狐耳（SSR）").cssClass("ear-ssr-fox").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("EYE").name("神狐目（SSR）").cssClass("eye-ssr-fox").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("FACE").name("神狐ほっぺ（SSR）").cssClass("face-ssr-fox").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("BODY").name("神狐ボディ（SSR）").cssClass("body-ssr-fox").rarity("SSR").build());
      avatarItemRepository.save(AvatarItemEntity.builder().type("ACCESSORY").name("神狐しっぽ（SSR）").cssClass("accessory-ssr-fox").rarity("SSR").build());

        }
    }
}
