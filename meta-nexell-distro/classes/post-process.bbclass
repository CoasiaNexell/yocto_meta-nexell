postprocess_common_function() {
    cd ${IMAGE_ROOTFS}
    ln -sf bin/busybox linuxrc
    rm -rf run
    ln -sf tmp run

    #---bootscript.sh create---
    echo "#!/bin/sh" > ./etc/bootscript.sh
    echo "mount -t devtmpfs none /dev" >> etc/bootscript.sh
    echo "mount -t proc none /proc" >> etc/bootscript.sh
    echo "mount -t sysfs none /sys" >> etc/bootscript.sh

    #ping permission change
    echo "inet:x:3003:root"    >> etc/group
    echo "net_raw:x:3004:root" >> etc/group

    echo "${PART_DATA_NODE} /data ext4 noatime,nosuid,nodev,nomblk_io_submit,errors=panic wait,check" >> etc/fstab

    echo "/dev/mmcblk0*" >> etc/udev/mount.blacklist
}

ROOTFS_POSTPROCESS_COMMAND += "postprocess_common_function;"