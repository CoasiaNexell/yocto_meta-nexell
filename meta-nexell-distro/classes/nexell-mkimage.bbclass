inherit nexell-secure

# to make images
# =================================================
# for nxp3220
# =================================================
DEPENDS += "${@bb.utils.contains('IMAGE_FSTYPES', 'ext4', 'android-tools-native', '', d)}"
DEPENDS += "${@bb.utils.contains('IMAGE_FSTYPES', 'multiubi2', 'mtd-utils-native', '', d)}"

make_ext4_image() {
	local label=$1 size=$2 dir=$3
	local output=$4

	echo "NOTE: MAKE EXT4: $label ($size) $dir -> $output"

	# change to sparse image "-s" optioin
	make_ext4fs -L ${label} -s -b 4k -l ${size} ${output} ${dir}
}

PART_BOOT_LABEL ?= "boot"
PART_BOOT_SIZE ?= ""
PART_BOOT_IMAGE ?= "boot.img"
UBOOT_LOGO_BMP ?= ""

make_boot_image() {
	local kern_dir=$1
	local kern_img=${kern_dir}/${KERNEL_IMAGETYPE} dtb_dir
	local boot_dir=${DEPLOY_DIR_IMAGE}/${PART_BOOT_LABEL}
	local boot_img=${DEPLOY_DIR_IMAGE}/${PART_BOOT_IMAGE}
	local logo_bmp=${UBOOT_LOGO_BMP}

	mkdir -p ${boot_dir}

	if [ -e ${kern_dir}/${KERNEL_DEVICETREE} ]; then
		dtb_file=${kern_dir}/${KERNEL_DEVICETREE}
	else
		dtb_file=${kern_dir}/${KERNEL_IMAGETYPE}-${KERNEL_DEVICETREE}
	fi

	cp ${kern_img} ${boot_dir}/
	cp ${dtb_file} ${boot_dir}/${KERNEL_DEVICETREE}

	if [ -f "${UBOOT_LOGO_BMP}" ]; then
		install -m 0644 ${logo_bmp} ${boot_dir};
	fi

	if ${@bb.utils.contains('IMAGE_FSTYPES','ext4','true','false',d)}; then
		if [ -z ${PART_BOOT_SIZE} ]; then
			echo "WARNING: NOT DEFINED 'PART_BOOT_SIZE'"
			return
		fi

		make_ext4_image ${PART_BOOT_LABEL} ${PART_BOOT_SIZE} ${boot_dir} ${boot_img}
	fi

	if ${@bb.utils.contains('IMAGE_FSTYPES','multiubi2','true','false',d)}; then
		if [ -z ${FLASH_PAGE_SIZE} ] || [ -z ${FLASH_BLOCK_SIZE} ] ||
		   [ -z ${FLASH_DEVICE_SIZE} ]; then
			bbfatal "Not defined 'FLASH_PAGE_SIZE or FLASH_BLOCK_SIZE or FLASH_DEVICE_SIZE'"
		fi

		local images="$(echo ${MKUBIFS_ARGS_boot} | awk -F',' '{ print NF }')"
		local i=1

		while [ $i -le $images ];
		do
		        args="$(echo $(echo ${MKUBIFS_ARGS_boot}| cut -d',' -f $i))"
			args="$args -p ${FLASH_PAGE_SIZE} -b ${FLASH_BLOCK_SIZE} -c ${FLASH_DEVICE_SIZE}"

			make_ubi_image ${args}

		        i=`expr $i + 1`
		done;
	fi
}

make_rootfs_image() {
	local root_name=$1
	local root_img=${DEPLOY_DIR_IMAGE}/$2
	local root_size=$3

	if ${@bb.utils.contains('IMAGE_FSTYPES','ext4','true','false',d)}; then
		if [ -z ${root_size} ]; then
			echo "WARNING: NOT DEFINED 'SIZE'"
			return
		fi

		if [ ! -e "${root_name}.ext4" ]; then
			echo "WARNING: NOT FOUND EXT4 ROOT FS: ${root_name}"
			return
		fi

		local fsname=$(readlink -f ${root_name}.ext4)
		local fssize=$(wc -c < ${fsname})
		echo "DEBUG: Resize ROOTS minimun size : ${fssize}:${fsname}"

		resize2fs ${root_name}.ext4 ${root_size};
		e2fsck -y -f ${root_name}.ext4;

		# change to sparse image
		ext2simg ${root_name}.ext4 ${root_img}
	fi
}

PART_DATA_LABEL ?= "data"
PART_DATA_SIZE ?= ""
PART_DATA_IMAGE ?= "userdata.img"

make_data_image() {
	local deploy_dir=${DEPLOY_DIR_IMAGE}
	local data_dir=${deploy_dir}/${PART_DATA_LABEL}
	local data_img=${deploy_dir}/${PART_DATA_IMAGE}

	mkdir -p ${data_dir}

	if ${@bb.utils.contains('IMAGE_FSTYPES','ext4','true','false',d)}; then
		if [ -z ${PART_DATA_SIZE} ]; then
			echo "WARNING: NOT DEFINED 'PART_DATA_SIZE'"
			return
		fi

		make_ext4_image ${PART_DATA_LABEL} ${PART_DATA_SIZE} ${data_dir} ${data_img}
	fi

	if ${@bb.utils.contains('IMAGE_FSTYPES','multiubi2','true','false',d)}; then
		if [ -z ${FLASH_PAGE_SIZE} ] || [ -z ${FLASH_BLOCK_SIZE} ] ||
		   [ -z ${FLASH_DEVICE_SIZE} ]; then
			bbfatal "Not defined 'FLASH_PAGE_SIZE or FLASH_BLOCK_SIZE or FLASH_DEVICE_SIZE'"
		fi

		local images="$(echo ${MKUBIFS_ARGS_data} | awk -F',' '{ print NF }')"
		local i=1

		while [ $i -le $images ];
		do
		        args="$(echo $(echo ${MKUBIFS_ARGS_data}| cut -d',' -f $i))"
			args="$args -p ${FLASH_PAGE_SIZE} -b ${FLASH_BLOCK_SIZE} -c ${FLASH_DEVICE_SIZE}"

			make_ubi_image ${args}

		        i=`expr $i + 1`
		done;
	fi
}

PART_MISC_LABEL ?= "misc"
PART_MISC_SIZE ?= ""
PART_MISC_IMAGE ?= "misc.img"

make_misc_image() {
	local deploy_dir=${DEPLOY_DIR_IMAGE}
	local misc_img=${deploy_dir}/${PART_MISC_IMAGE}
	local misc_dir=${deploy_dir}/${PART_MISC_LABEL}
	local etc_dir=${misc_dir}/etc

	# remove old misc directory
	if [ -d ${misc_dir} ]; then
		rm -rf ${misc_dir}
	fi

	mkdir -p ${misc_dir}
	mkdir -p ${etc_dir}

        # install swupdate signing key file
        #
        if [ ! -z ${SWU_SIGN_PUBLIC_KEY} ] && [ -f ${SWU_SIGN_PUBLIC_KEY} ]; then
                install -m 644 ${SWU_SIGN_PUBLIC_KEY} ${etc_dir}/swu.public.key
        fi

        # install swupdate version
        #
        if [ ! -z "${SWU_HW_REVISION_TARGET}" ]; then
                echo ${SWU_HW_REVISION_TARGET} > ${etc_dir}/hwrevision
        fi

        if [ ! -z ${SWU_SW_VERSION_FILE} ] && [ -f ${SWU_SW_VERSION_FILE} ]; then
                install -m 644 ${SWU_SW_VERSION_FILE} ${etc_dir}/sw-versions
        fi

	# generate misc.img
	#
	if ${@bb.utils.contains('IMAGE_FSTYPES','ext4','true','false',d)}; then
		if [ -z ${PART_MISC_SIZE} ]; then
			echo "WARNING: NOT DEFINED 'PART_MISC_SIZE'"
			return
		fi

		make_ext4_image ${PART_MISC_LABEL} ${PART_MISC_SIZE} ${misc_dir} ${misc_img}
	fi

	if ${@bb.utils.contains('IMAGE_FSTYPES','multiubi2','true','false',d)}; then
		if [ -z ${FLASH_PAGE_SIZE} ] || [ -z ${FLASH_BLOCK_SIZE} ] ||
		   [ -z ${FLASH_DEVICE_SIZE} ]; then
			bbfatal "Not defined 'FLASH_PAGE_SIZE or FLASH_BLOCK_SIZE or FLASH_DEVICE_SIZE'"
		fi

		local images="$(echo ${MKUBIFS_ARGS_misc} | awk -F',' '{ print NF }')"
		local i=1

		while [ $i -le $images ];
		do
		        args="$(echo $(echo ${MKUBIFS_ARGS_misc}| cut -d',' -f $i))"
			args="$args -p ${FLASH_PAGE_SIZE} -b ${FLASH_BLOCK_SIZE} -c ${FLASH_DEVICE_SIZE}"

			make_ubi_image ${args}

		        i=`expr $i + 1`
		done;
	fi
}

EXTRA_ROOTFS_DIR ?= "${BASE_WORKDIR}/extra-rootfs-support"

make_rootfs_extra() {
	if [ ! -d "${EXTRA_ROOTFS_DIR}" ]; then
		echo "WARNING: not found ${EXTRA_ROOTFS_DIR}"
		return
	fi

	cp -dr ${EXTRA_ROOTFS_DIR}/* ${IMAGE_ROOTFS}
}

convert_hn_to_byte() {
	local val=$1
	local ret=$2 # store calculated byte
	local delmi="" mulitple=0

	case "$val" in
        *K* ) delmi='K'; mulitple=1024;;
        *k* ) delmi='k'; mulitple=1024;;
        *M* ) delmi='M'; mulitple=1048576;;
        *m* ) delmi='m'; mulitple=1048576;;
        *G* ) delmi='G'; mulitple=1073741824;;
        *g* ) delmi='g'; mulitple=1073741824;;
        -- ) ;;
        esac

        if [ ! -z $delmi ]; then
                val=$(echo $val| cut -d$delmi -f 1)
		val=`expr $val \* $mulitple`
		eval "$ret=\"${val}\""
        fi
}

create_ubi_ini_file() {
	local ini_file=$1 image=$2 vname=$3 vid=$4 vsize=$5

        echo \[ubifs\] > $ini_file
        echo mode=ubi >> $ini_file
        echo image=$image >> $ini_file
        echo vol_id=$vid >> $ini_file
        echo vol_size=$vsize >> $ini_file
        echo vol_type=dynamic >> $ini_file
        echo vol_name=$vname >> $ini_file
        echo vol_flags=autoresize >> $ini_file
}

# -r : root image
# -v : volume name
# -l : volume size
# -i : volume id
# -p : page size
# -s : sub page size
# -b : block size
# -c : flash size
make_ubi_image() {
	ARGS=$(getopt -o p:s:b:c:r:v:l:i: -- "$@");
	eval set -- "$ARGS";

	local volume_id=0

	while true; do
		case "$1" in
		-p ) page_size=$2; shift 2;;
		-s ) sub_page_size=$2; shift 2;;
		-b ) block_size=$2; shift 2;;
		-c ) flash_size=$2; shift 2;;
		-r ) root=$2; shift 2;;
		-v ) volume_name=$2; shift 2;;
		-l ) volume_size=$2; shift 2;;
		-i ) volume_id=$2; shift 2;;
		-- ) break ;;
		esac
	done

	if [ -z $page_size ] || [ -z $block_size ] || [ -z $flash_size ]; then
		bbfatal "Not defined ubi page size '-p' or block size '-b' or flash size '-c'"
	fi

	if [ -z $sub_page_size ] ||  [ $sub_page_size -eq 0 ]; then
		sub_page_size=$page_size
	fi

	if [ -z $root ] || [ -z $volume_name ] || [ -z $volume_size ]; then
		bbfatal "Not defined ubi root file '-r' volume name '-o' or ubi volume size '-l'"
	fi

	convert_hn_to_byte $block_size block_size
	convert_hn_to_byte $flash_size flash_size
	convert_hn_to_byte $volume_size volume_size

	#
	# Calcurate UBI varialbe
	# Refer to http://processors.wiki.ti.com/index.php/UBIFS_Support
	#
	local LEB=`expr $block_size - \( 2 \* $page_size \)`
	local PEB=$block_size
	local BLC=`expr $flash_size / $block_size`
	local RPB=`expr \( 20 \* $BLC \) / 1024`
	local RPC=`expr $PEB - $LEB`
	local TPB=`expr $volume_size / $PEB`
	local OVH=`expr \( \( $RPB + 4 \) \* $PEB \) + \( $RPC \* \( $TPB - $RPB - 4 \) \)`
	local OVB=`expr $OVH / $PEB`

	local avail_size=`expr $volume_size - $OVH`
	local max_block_count=`expr $avail_size / $LEB`

	local deploy_dir=${IMGDEPLOYDIR}
	if [ -z $img_deplay_dir ]; then
		deploy_dir=${DEPLOY_DIR_IMAGE}
	fi

	local ubi_fs=$deploy_dir/$volume_name.ubifs
	local ubi_image=$deploy_dir/$volume_name.img
	local ubi_ini=$deploy_dir/ubinize-$volume_name.cfg

	echo "DEBUG: ROOT dir = $root"
	echo "DEBUG: UBI fs = $ubi_fs"
	echo "DEBUG: UBI image = $ubi_image"
	echo "DEBUG: UBI ini = $ubi_ini"
	echo "DEBUG: UBI Volume name = $volume_name"
	echo "DEBUG: UBI Volume id = $volume_id"
	echo "DEBUG: UBI Volume size = `expr $avail_size / 1024 / 1024`MiB (`expr $volume_size / 1024 / 1024`MiB)"
	echo "DEBUG: UBI Logical Erase Block size = `expr $LEB / 1024`KiB"
	echo "DEBUG: UBI Maximum Lobical Erase block counts = $max_block_count"
	echo "DEBUG: UBI Overhead = $OVB ($TPB)"
	echo "DEBUG: UBI Reserved size = `expr $OVH / 1024 / 1024`MiB"
	echo "DEBUG: Flash Page size = $page_size"
	echo "DEBUG: Flash Sub page size = $sub_page_size"
	echo "DEBUG: Flash Block size = `expr $block_size / 1024`KiB"
	echo "DEBUG: Flash size = `expr $flash_size / 1024 / 1024`MiB"

	mkdir -p $root

	create_ubi_ini_file $ubi_ini $ubi_fs \
		$volume_name $volume_id `expr $avail_size / 1024 / 1024`MiB

	mkfs.ubifs -r $root -o $ubi_fs -m $page_size -e $LEB -c $max_block_count -F

	ubinize -o $ubi_image -m $page_size -p $block_size -s $sub_page_size $ubi_ini
}

# =================================================
# for s5p4418/s5p6818
# =================================================
make_output_dir() {
    if [ ! -d ${BSP_OUTPUT_DIR_PATH} ];then
        mkdir -p ${BSP_OUTPUT_DIR_PATH}/tools
        chmod 777 ${BSP_OUTPUT_DIR_PATH}
		chmod 777 ${BSP_OUTPUT_DIR_PATH}/tools
    fi
}

copy_file_to_output() {
	local in_file=$1

	make_output_dir

	if [ -f ${in_file} ]; then
        cp -af ${in_file} ${BSP_OUTPUT_DIR_PATH}
    fi
}

copy_board_partmap() {
    local out_dir=$1
    if [ ${BSP_TARGET_IMAGE_TYPE} = "ubuntu" ]; then
        cp -af ${NEXELL_BOARD_PARTMAP_PATH}/partmap_emmc_${BSP_TARGET_MACHINE}-ubuntu.txt ${out_dir}/partmap_emmc.txt
    else
        cp -af ${NEXELL_BOARD_PARTMAP_PATH}/partmap_emmc_${BSP_TARGET_MACHINE}.txt ${out_dir}/partmap_emmc.txt
    fi
}

copy_kernel_image() {
	echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m copy_kernel_image \033[0m"
    echo "\033[40;33m -------------------------------------------------  \033[0m"
	echo "\033[40;33m src_path : '$1' \033[0m"
    echo "\033[40;33m dst_path : '$2' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"
    local src_path=$1 dst_path=$2

    copy_file_to_output ${src_path}/boot.img

    if [ ${BSP_TARGET_IMAGE_TYPE} = "ubuntu" ];then
        if [ -d ${TOPDIR}/tmp/work/linux-kernel-selftests ]; then
            cp -a ${TOPDIR}/tmp/work/selftests ${TOPDIR}/tmp/work/extra-rootfs-support/usr/bin/
        fi
    fi
}

copy_rootfs_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m copy_rootfs_image \033[0m"
    echo "\033[40;33m -------------------------------------------------  \033[0m"
	echo "\033[40;33m src_path : '$1' \033[0m"
    echo "\033[40;33m dst_path : '$2' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"
	local src_path=$1 dst_path=$2

    if [ ${BSP_TARGET_IMAGE_TYPE} = "ubuntu" ];then
        rm -rf ${dst_path}/*.ext4
        rm -rf ${dst_path}/rootfs.img

        echo "\033[40;33m  >>>>   download ubuntu image        \033[0m"
        wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION}${UBUNTU_ROOTFS} -P ${dst_path}
        mv ${RESULT_PATH}/${UBUNTU_ROOTFS} ${dst_path}/rootfs.tar.gz

        echo "\033[40;33m  >>>>   copy_extra-rootfs-support to result dir        \033[0m"
        sudo cp -a ${TOPDIR}/tmp/work/extra-rootfs-support ${dst_path}

        # s5p6818 binary use armhf version, so kselftest used armhf version too.
        # kselftest do not build. using prebuilt binary
        if [ ${NEXELL_BOARD_SOCNAME} = "s5p6818" ]; then
            echo "\033[40;33m  >>>>   extract kselftests        \033[0m"
            sudo rm -rf ${dst_path}/extra-rootfs-support/usr/bin/kselftests
            sudo rm -rf ${dst_path}/kselftests.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_KSELFTESTS} -P ${dst_path}
            sudo tar --overwrite -xvzf ${dst_path}/kselftests.tar.gz -C ${dst_path}/extra-rootfs-support/usr/bin/

            echo -e "\033[40;33m  >>>>   extract testsuites        \033[0m"
            sudo rm -rf ${dst_path}/testsuite.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_NX_TESTSUITE} -P ${dst_path}
            sudo tar --overwrite -xvzf ${dst_path}/testsuite.tar.gz -C ${dst_path}/extra-rootfs-support/usr/

            echo -e "\033[40;33m  >>>>   extract nexell libraries   \033[0m"
            sudo rm -rf ${dst_path}/nxlibs.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_NX_LIBS} -P ${dst_path}
            sudo tar --overwrite -xvzf ${dst_path}/nxlibs.tar.gz -C ${dst_path}/extra-rootfs-support/usr/
        fi
    else
        if [ -f ${src_path}/${IMAGE_BASENAME}-${MACHINE}.tar.bz2 ]; then
            cp ${src_path}/${IMAGE_BASENAME}-${MACHINE}.tar.bz2 ${dst_path}
        fi
        if [ -f ${src_path}/${IMAGE_BASENAME}-${MACHINE}.ext4 ]; then
            cp ${src_path}/${IMAGE_BASENAME}-${MACHINE}.ext4 ${dst_path}
        fi
    fi

    cp ${NEXELL_FUSING_TOOLS_PATH}/partition.txt ${dst_path}
}

copy_fusing_tools() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m copy_fusing_tools \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
	echo "\033[40;33m out_dir : '$1' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local out_dir=$1
    # step1 : copy scripts & tools
    #if [ ! -d ${out_dir}/tools ];then
        # flashing tool copy
        mkdir -p ${out_dir}/tools

        if [ ${BSP_TARGET_SOCNAME} = "nxp3220" ];then
            echo "to do"
        else
            cp -af ${NEXELL_FUSING_TOOLS_PATH}/standalone-fastboot-download.sh ${out_dir}/tools
            cp -af ${NEXELL_FUSING_TOOLS_PATH}/standalone-uboot-by-usb-download.sh ${out_dir}/tools
            cp -af ${NEXELL_FUSING_TOOLS_PATH}/usb-downloader ${out_dir}/tools
        fi
    #fi

    # step2 : copy bl1 image & partition info files
    copy_board_partmap ${out_dir}/tools

    if [ ${BSP_TARGET_SOCNAME} = "nxp3220" ];then
        echo "to do"
    else
        if ls ${out_dir}/bl1-*.bin 1> /dev/null 2>&1; then
            cp -af ${out_dir}/bl1-*.bin ${out_dir}/tools
        else
            echo "bl1-*.bin do not exists"
        fi
        if [ -f ${out_dir}/partmap_emmc.txt ]; then
            cp -af ${out_dir}/partmap_emmc.txt ${out_dir}/tools
        fi
        if [ -f ${out_dir}/partition.txt ]; then
            cp -af ${out_dir}/partition.txt ${out_dir}/tools
        fi
    fi

    touch ${out_dir}/YOCTO.${BSP_OUTPUT_DIR_NAME}.INFO.DoNotChange
}

make_2ndboot_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_2ndboot_for_emmc \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m soc_name : '$1' \033[0m"
    echo "\033[40;33m in_img : '$2' \033[0m"
    echo "\033[40;33m out_img : '$3' \033[0m"
    echo "\033[40;33m aes_key : '$4' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local soc_name=$1 in_img=$2 out_img=$3 aes_key=$4 hash_name=$5

    if [ -z ${aes_key} ]; then
        aes_key='""'
    fi

    if [ -z ${hash_name} ]; then
        hash_name='""'
    fi

    if [ ${soc_name} = "s5p6818" ]; then
        if [ ${NEXELL_SECURE_BOOT} = "true" ]; then
            do_gen_hash_rsa ${in_img} ${hash_name} ${aes_key}
            dd if=${in_img}.pub of=${in_img} ibs=256 count=1 obs=512 seek=1 conv=notrunc
            return
            do_aes_encrypt ${out_img} ${in_img} ${aes_key}
        else
            cp ${in_img} ${out_img}
        fi
    else
        echo "SECURE BOOT is not support in ${soc_name}"
    fi

    # copy 2ndboot image to output directory
    copy_file_to_output ${out_img}
}

make_3rdboot_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_3rdboot_image \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m soc_name : '$1' \033[0m"
    echo "\033[40;33m in_img : '$2' \033[0m"
    echo "\033[40;33m out_img : '$3' \033[0m"
    echo "\033[40;33m load_addr : '$4' \033[0m"
    echo "\033[40;33m jump_addr : '$5' \033[0m"
    echo "\033[40;33m extra_opts : '$6' \033[0m"
    echo "\033[40;33m dev_id : '$7' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local soc_name=$1
    local in_img=$2
    local out_img=$3
    local load_addr=$4
    local jump_addr=$5
    local extra_opts="$6"
    local dev_id=$7

    do_secure_bingen $soc_name \
            "3rdboot" \
            $in_img \
            $out_img \
            $load_addr \
            $jump_addr \
            "$extra_opts" \
            $dev_id

    # copy 3ndboot image to output directory
    copy_file_to_output ${out_img}
}

make_ramdisk_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_ramdisk_image \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m arm_arch : '$1' \033[0m"
    echo "\033[40;33m in_img : '$2' \033[0m"
    echo "\033[40;33m out_path : '$3' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local arm_arch=$1 in_img=$2 out_path=$3

    mkdir -p ${out_path}/boot
    rm -rf ${out_path}/boot/uInitrd

	${NEXELL_TOOL_MKIMAGE} -A ${arm_arch} -O linux -T ramdisk \
            -C none -a 0 -e 0 -n uInitrd -d ${in_img} \
            ${out_path}/boot/uInitrd
}

make_environment_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_environment_image \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m partition_size : '$1' \033[0m"
    echo "\033[40;33m out_img : '$2' \033[0m"
    echo "\033[40;33m envs_file : '$3' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local partition_size=$1 out_img=$2 envs_file=$3

    ${NEXELL_TOOL_MKENVIMAGE} -s ${partition_size} -o ${out_img} ${envs_file}

    # copy environment image to output directory
    copy_file_to_output ${out_img}
    copy_file_to_output ${envs_file}
}

make_bootimg() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_bootimg \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m src_path : '$1' \033[0m"
    echo "\033[40;33m dst_path : '$2' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local src_path=$1 dst_path=$2

    # remove old files
    mkdir -p ${dst_path}/boot
    rm -rf ${dst_path}/boot/*.dtb
    rm -rf ${dst_path}/boot/${NEXELL_KERNEL_IMAGE_NAME}

    # copy dtb files
    find ${src_path} -name "*.dtb" -type f -exec cp {} ${dst_path}/boot \;

    # copy kernel image
    cp -aL ${src_path}/${NEXELL_KERNEL_IMAGE_NAME} ${dst_path}/boot
    cp ${NEXELL_BOOTLOGO_PATH}/logo.bmp ${dst_path}/boot/

    # make boot.img
    ${NEXELL_TOOL_MAKE_EXT4FS} -s -l ${NEXELL_BOOT_PARTITION_SIZE} ${dst_path}/boot.img ${dst_path}/boot/

    # copy to result directory
	copy_kernel_image ${dst_path} ${BSP_OUTPUT_DIR_PATH}
}

make_sparse_rootfs_img() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_sparse_rootfs_img \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m img_type : '$1' \033[0m"
    echo "\033[40;33m in_ext4_img : '$2' \033[0m"
    echo "\033[40;33m partition_size : '$3' \033[0m"
    echo "\033[40;33m dst_path : '$4' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local img_type=$1 in_ext4_img=$2 partition_size=$3 dst_path=$4

    if [ ${img_type} = "ubuntu" ]; then
        ${NEXELL_TOOL_MKROOTFS_IMAGE} \
                ${dst_path} \
                ${in_ext4_img} \
                4096 \
                ${dst_path}/extra-rootfs-support
    fi

    ${NEXELL_TOOL_EXT2SIMG} ${in_ext4_img} ${dst_path}/rootfs.img

    rm -rf ${dst_path}/userdata
    mkdir -p ${dst_path}/userdata

    ${NEXELL_TOOL_MAKE_EXT4FS} -s -l ${partition_size} -b 4K -a user ${dst_path}/userdata.img ${dst_path}/userdata
    echo "userdata partition size : ${partition_size}byte"

    if [ ${BSP_TARGET_BOARD_NAME} = "convergence-daudio" ]; then
        rm -rf ${dst_path}/svmdata
        cp -af ${BSP_ROOT_DIR}/vendor/nexell/apps/svm_daemon/data/ ${dst_path}/svmdata
        ${NEXELL_TOOL_MAKE_EXT4FS} -s -l 33554432 -b 4K -a user ${dst_path}/svmdata.img ${dst_path}/svmdata

        # copy image to output directory
        copy_file_to_output ${dst_path}/svmdata.img
    fi

    # copy image to output directory
    copy_file_to_output ${dst_path}/rootfs.img
    copy_file_to_output ${dst_path}/userdata.img

    copy_rootfs_image ${dst_path} ${BSP_OUTPUT_DIR_PATH}
}

make_fip_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_fip_image \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m in_img : '$1' \033[0m"
    echo "\033[40;33m out_img : '$2' \033[0m"
    echo "\033[40;33m seek_val : '$3' \033[0m"
    echo "\033[40;33m bs_val : '$4' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local in_img=$1 out_img=$2 seek_val=$3 bs_val=$4
    dd if=${in_img} of=${out_img} seek=${seek_val} bs=${bs_val}

    # copy image to output directory
    copy_file_to_output ${out_img}
}