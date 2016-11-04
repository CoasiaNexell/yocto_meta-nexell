postprocess_common_function() {
    cd ${IMAGE_ROOTFS}    
    ln -sf bin/busybox linuxrc
    ln -sf lib lib64
    rm -rf run
    ln -sf tmp run

    #---bootscript.sh create---
    echo "#!/bin/sh" > ./etc/bootscript.sh
    echo "mount -t devtmpfs none /dev" >> etc/bootscript.sh
    echo "mount -t proc none /proc" >> etc/bootscript.sh
    echo "mount -t sysfs none /sys" >> etc/bootscript.sh
}

ROOTFS_POSTPROCESS_COMMAND += "postprocess_common_function;"