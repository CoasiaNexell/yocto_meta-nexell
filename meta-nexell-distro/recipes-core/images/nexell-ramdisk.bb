inherit post-process-ramdisk

SPALSH = "psplash-nexell"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

#IMAGE_INSTALL = "busybox base-passwd initramfs-boot bash"
IMAGE_FEATURES = ""
IMAGE_ROOTFS_SIZE = "16384"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
export IMAGE_BASENAME = "core-image-minimal-initramfs"

IMAGE_LINGUAS = ""

# NOTE we must use cpio.gz here as this is what mkbootimg requires
IMAGE_FSTYPES_forcevariable = "cpio.gz"

# We don't need depmod data here
KERNELDEPMODDEPEND = ""
USE_DEPMOD = "0"
PACKAGE_INSTALL += "systemd busybox android-tools-nexell e2fsprogs bash psplash util-linux-sfdisk udev-extraconf"

inherit core-image

require recipes-extended/images/swupdate-image.inc

do_recovery_image() {
    # generate swu image
    make_recovery_image "${SWU_UPDATE_SWU_IMAGE}"
}
addtask recovery_image after do_image_complete before do_build

do_recovery_image[nostamp] = "1"

DEPENDS_append = " update-rc.d-native"

PACKAGE_INSTALL += " \
    swupdate \
    swupdate-tools \
	swupdate-recovery \
    ${@ 'psplash' if d.getVar('SWU_PROGRESS_PSPLASH') else ''} \
    udev \
	udev-extraconf \
    util-linux-agetty \
	nexell-init \
    "

postprocess_recovery_image () {
    local rootdir=${IMAGE_ROOTFS}

    # misc partition add to /etc/fstab for ext4
    mkdir -p ${rootdir}/misc
    echo "/dev/mmcblk0*" >> ${rootdir}${sysconfdir}/udev/mount.blacklist

    rm ${rootdir}/etc/udev/rules.d/automount.rules

    if [ ! -z "${SWU_SERVICE_PROGRESS_RECOVERY}" ]; then
        sed -i -e 's,ExecStartPre=.*,ExecStartPre=/etc/init.d/psplash.sh start,g' \
            ${rootdir}${systemd_system_unitdir}/swupdate-progress.service
        sed -i -e 's,ExecStart=.*,ExecStart=${SWU_SERVICE_PROGRESS_RECOVERY},g' \
            ${rootdir}${systemd_system_unitdir}/swupdate-progress.service
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "postprocess_recovery_image;"
