inherit linux-nexell-base
inherit nexell-mkimage


image_postprocess_tiny_function() {

	make_sparse_rootfs_img "tiny" \
			${IMGDEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.tar.bz2 \
			${NEXELL_ROOT_PARTITION_SIZE} \
			${NEXELL_USER_PARTITION_SIZE} \
			${DEPLOY_DIR_IMAGE}

	copy_fusing_tools ${BSP_OUTPUT_DIR_PATH}
}

IMAGE_POSTPROCESS_COMMAND += "image_postprocess_tiny_function;"
