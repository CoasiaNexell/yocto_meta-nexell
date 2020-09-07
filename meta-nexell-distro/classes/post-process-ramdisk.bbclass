inherit nexell-mkimage

image_postprocess_ramdisk_function() {
    # make ramdik image
    # 1:${arm_arch}
    # 2:${in_img}
    # 3:${arm_arch}
    # output : ${DEPLOY_DIR_IMAGE}/boot/uInitrd
    make_ramdisk_image ${ARM_ARCH} \
            ${IMGDEPLOYDIR}/${IMAGE_BASENAME}-${BSP_TARGET_MACHINE}.cpio.gz \
            ${DEPLOY_DIR_IMAGE}
}

IMAGE_POSTPROCESS_COMMAND += "image_postprocess_ramdisk_function;"