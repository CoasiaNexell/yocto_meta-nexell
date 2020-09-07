inherit linux-nexell-base

DEFAULT_SDL_PLATFORM ?= "${@bb.utils.contains('DISTRO_FEATURES', 'sdl-default-platform-eglfs', 'FB', \
                           bb.utils.contains('DISTRO_FEATURES', 'sdl-default-platform-wayland', 'WAYLAND', '', d), d)}"

postprocess_sdl_function() {
    cd ${IMAGE_ROOTFS}

    # adbd do not start on boot time, If you need use adbd, then you can  run /usr/bin/start_adbd.sh
    cd ${IMAGE_ROOTFS}/lib/systemd/system
    sed -i 's/ExecStart=\/usr\/bin\/adbd/\#ExecStart=\/usr\/bin\/adbd/g' android-tools-adbd.service
    # sed -i 's/Restart=always/Restart=no/g' systemd-timesyncd.service

    cd ${IMAGE_ROOTFS}/usr/lib
    touch libMali_for_${DEFAULT_SDL_PLATFORM}
    if [ "${ARCH_TYPE_NUM}" -eq "64" ]; then
        # 64bit FB library does not support, TBD
        ln -sf ./nexell/libMali-${ARCH_TYPE_NUM}-WAYLAND libMali.so
    else
        ln -sf ./nexell/libMali-${ARCH_TYPE_NUM}-${DEFAULT_SDL_PLATFORM}.so libMali.so
    fi

    # wayland 1.15 only
    #    mv libwayland-egl.so.1.0.0 libwayland-egl.so.org
    #    ln -sf libMali.so libwayland-egl.so.1.0.0

    #automount iocharset modified
    cd ${IMAGE_ROOTFS}
    cp ${BASE_WORKDIR}/use-post-process/mount.sh etc/udev/scripts/
}

ROOTFS_POSTPROCESS_COMMAND += "postprocess_sdl_function;"

image_postprocess_sdl_wayland_function() {

    make_sparse_rootfs_img "sdl-wayland" \
		    ${IMGDEPLOYDIR}/${IMAGE_BASENAME}-${MACHINE}.ext4 \
			${NEXELL_USER_PARTITION_SIZE} \
			${DEPLOY_DIR_IMAGE}
}

IMAGE_POSTPROCESS_COMMAND += "image_postprocess_sdl_wayland_function;"
