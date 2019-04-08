DEPENDS += "android-tools-native"

make_ext4_image() {
	PART_LABEL=$1
	PART_SIZE=$2
	PART_DIR=$3
	OUTPUT_IMAGE=$4

	echo "NOTE: MAKE EXT4: ${PART_LABEL} (${PART_SIZE}) ${PART_DIR} -> ${OUTPUT_IMAGE}"

	# change to sparse image "-s" optioin
	make_ext4fs -L ${PART_LABEL} -s -b 4k -l ${PART_SIZE} ${OUTPUT_IMAGE} ${PART_DIR}
}

PART_BOOT_LABEL ?= "boot"
PART_BOOT_SIZE ?= ""
UBOOT_LOGO_BMP ?= ""

make_ext4_bootimg() {
	S_DIR=$1
	D_DIR=${DEPLOY_DIR_IMAGE}/boot
	IMAGE=${DEPLOY_DIR_IMAGE}/boot.img
	LOGO=${UBOOT_LOGO_BMP}

	if [ -z ${PART_BOOT_SIZE} ]; then
		echo "WARNING: NOT DEFINED 'PART_BOOT_SIZE'"
		return
	fi

	mkdir -p ${D_DIR}

	if [ -e ${S_DIR}/${KERNEL_DEVICETREE} ]; then
		DTBFILE=${S_DIR}/${KERNEL_DEVICETREE}
	else
		DTBFILE=${S_DIR}/${KERNEL_IMAGETYPE}-${KERNEL_DEVICETREE}
	fi

	IMGFILE=${S_DIR}/${KERNEL_IMAGETYPE}
	cp ${IMGFILE} ${D_DIR}
	cp ${DTBFILE} ${D_DIR}/${KERNEL_DEVICETREE}

	if [ -f ${UBOOT_LOGO_BMP} ]; then
		install -m 0644 ${LOGO} ${D_DIR};
	fi

	make_ext4_image ${PART_BOOT_LABEL} ${PART_BOOT_SIZE} ${D_DIR} ${IMAGE}
}

PART_ROOTFS_LABEL ?= "rootfs"
PART_ROOTFS_SIZE ?= ""

make_ext4_rootfsimg() {
	ROOT_FS=$1
	ROOT_DIR=${DEPLOY_DIR_IMAGE}/rootfs
	IMAGE=${DEPLOY_DIR_IMAGE}/rootfs.img

	if [ -z ${PART_ROOTFS_SIZE} ]; then
		echo "WARNING: NOT DEFINED 'PART_ROOTFS_SIZE'"
		return
	fi

	if [ ! -e "${ROOT_FS}.ext4" ]; then
		echo "WARNING: NOT FOUND EXT4 ROOT FS: ${ROOT_FS}"
		return
	fi

	fsname=$(readlink -f ${ROOT_FS}.ext4)
	fssize=$(wc -c < ${fsname})
	echo "DEBUG: Resize ROOTS minimun size : ${fssize}:${fsname}"

	resize2fs ${ROOT_FS}.ext4 ${PART_ROOTFS_SIZE};
	e2fsck -y -f ${ROOT_FS}.ext4;

	# change to sparse image
	ext2simg ${ROOT_FS}.ext4 ${IMAGE}
}

EXTRA_ROOTFS_DIR ?= "${BASE_WORKDIR}/extra-rootfs-support"

make_extra_rootfs() {
	if [ ! -d ${EXTRA_ROOTFS_DIR} ]; then
		echo "WARNING: not found ${EXTRA_ROOTFS_DIR}"
		return
	fi

	cp -dr ${EXTRA_ROOTFS_DIR}/* ${IMAGE_ROOTFS}
}

PART_DATA_LABEL ?= "boot"
PART_DATA_SIZE ?= ""

make_ext4_dataimg() {
	S_DIR=$1
	D_DIR=${DEPLOY_DIR_IMAGE}/data
	IMAGE=${DEPLOY_DIR_IMAGE}/userdata.img

	if [ -z ${PART_DATA_SIZE} ]; then
		echo "WARNING: NOT DEFINED 'PART_DATA_SIZE'"
		return
	fi

	mkdir -p ${D_DIR}
	make_ext4_image ${PART_DATA_LABEL} ${PART_DATA_SIZE} ${D_DIR} ${IMAGE}
}