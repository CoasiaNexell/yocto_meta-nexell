postprocess_install_daudio_cona_sdk_function() {
    install -d ${IMAGE_ROOTFS}/etc/bluetooth
    install -d ${IMAGE_ROOTFS}/usr/bin
    install -d ${IMAGE_ROOTFS}/usr/lib
    install -d ${IMAGE_ROOTFS}/nexell/daudio
    install -d ${IMAGE_ROOTFS}/podo/apps/pdwindowcompositor

    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/etc/bluetooth/*	${IMAGE_ROOTFS}/etc/bluetooth/
    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/usr/bin/*		${IMAGE_ROOTFS}/usr/bin/
    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/usr/lib/*		${IMAGE_ROOTFS}/usr/lib/
    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/nexell/daudio/*	${IMAGE_ROOTFS}/nexell/daudio/
    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/podo/apps/pdwindowcompositor/* ${IMAGE_ROOTFS}/podo/apps/pdwindowcompositor/
    cp -aR ${PWD}/../meta-nexell/recipes-sdk/qt/daudio-cona-sdk/files/nexell/daudio/fms.os.sdk.version ${IMAGE_ROOTFS}/nexell/daudio/
	if test -e ${IMAGE_ROOTFS}/etc/systemd/system/sync-clocks.service ; then
		rm ${IMAGE_ROOTFS}/etc/systemd/system/sync-clocks.service
	fi
	rm ${IMAGE_ROOTFS}/etc/systemd/system/sysinit.target.wants/sync-clocks.service

}

ROOTFS_POSTPROCESS_COMMAND += "postprocess_install_daudio_cona_sdk_function;"