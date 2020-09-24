inherit nexell-secure

# to copy image
# =================================================
# for all
# =================================================
make_output_dir() {
    if [ ! -d ${BSP_OUTPUT_DIR_PATH} ];then
        if [ ${BSP_TARGET_SOCNAME} = "nxp3220" ];then
            mkdir -p ${BSP_OUTPUT_DIR_PATH}
            chmod 777 ${BSP_OUTPUT_DIR_PATH}
        else
            mkdir -p ${BSP_OUTPUT_DIR_PATH}/tools
            chmod 777 ${BSP_OUTPUT_DIR_PATH}
            chmod 777 ${BSP_OUTPUT_DIR_PATH}/tools
        fi
    fi
}

copy_file_to_output() {
	local in_file=$1

	make_output_dir

	#if [ -f ${in_file} ]; then
	if ls ${in_file} 1> /dev/null 2>&1; then
		cp -af ${in_file} ${BSP_OUTPUT_DIR_PATH}
	fi
}

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

	# copy image to output dir
	copy_file_to_output ${output}
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

		# Change image generation tool from ext2simg to make_ext4fs
		#local fsname=$(readlink -f ${root_name}.ext4)
		#local fssize=$(wc -c < ${fsname})
		#echo "DEBUG: Resize ROOTS minimun size : ${fssize}:${fsname}"

		##resize2fs ${root_name}.ext4 ${root_size};
		#e2fsck -y -f ${root_name}.ext4;

		## change to sparse image
		#ext2simg ${root_name}.ext4 ${root_img}

		make_ext4fs -s -b 4k -l ${root_size} ${root_img} ${IMAGE_ROOTFS}

		# copy image to output dir
		copy_file_to_output ${root_img}
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

	# copy image to output dir
	copy_file_to_output ${ubi_image}
}

# =================================================
# for s5p4418/s5p6818
# =================================================
copy_board_partmap() {
    local out_dir=$1
    cp -af ${NEXELL_BOARD_PARTMAP_PATH}/partmap_emmc_${BSP_TARGET_MACHINE}.txt ${out_dir}/partmap_emmc.txt
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
}

copy_rootfs_image() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m copy_rootfs_image \033[0m"
    echo "\033[40;33m -------------------------------------------------  \033[0m"
	echo "\033[40;33m src_path : '$1' \033[0m"
    echo "\033[40;33m dst_path : '$2' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"
	local src_path=$1 dst_path=$2

    if [ -f ${src_path}/${IMAGE_BASENAME}-${MACHINE}.tar.bz2 ]; then
        cp ${src_path}/${IMAGE_BASENAME}-${MACHINE}.tar.bz2 ${dst_path}
    fi
    if [ -f ${src_path}/${IMAGE_BASENAME}-${MACHINE}.ext4 ]; then
        cp ${src_path}/${IMAGE_BASENAME}-${MACHINE}.ext4 ${dst_path}
    fi

    if [ -f ${src_path}/partition.txt ]; then
        mkdir -p ${dst_path}/tools
        cp ${src_path}/partition.txt ${dst_path}/tools
    fi
}

copy_fusing_tools() {
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m copy_fusing_tools \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
	echo "\033[40;33m out_dir : '$1' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local out_dir=$1

    if [ ${BSP_TARGET_SOCNAME} = "nxp3220" ];then
        # step1 : copy scripts
        cp -af ${NEXELL_TOOLS_SCRIPTS_PATH}/partmap_*.sh ${out_dir}
        cp -af ${NEXELL_TOOLS_SCRIPTS_PATH}/usb-down.sh ${out_dir}
        cp -af ${NEXELL_TOOLS_SCRIPTS_PATH}/configs/udown.bootloader*.sh ${out_dir}

        # step2 : copy bin
        cp -af ${NEXELL_TOOLS_BIN_PATH}/linux-usbdownloader ${out_dir}
        cp -af ${NEXELL_TOOLS_BIN_PATH}/simg2dev ${out_dir}

        # step3 : copy files
        if ls ${NEXELL_TOOLS_FILES_PATH}/partmap_*.txt 1> /dev/null 2>&1; then
            cp -af ${NEXELL_TOOLS_FILES_PATH}/partmap_*.txt ${out_dir}
        fi
        if ls ${NEXELL_TOOLS_FILES_PATH}/linux-partmap_*.txt 1> /dev/null 2>&1; then
            cp -af ${NEXELL_TOOLS_FILES_PATH}/linux-partmap_*.txt ${out_dir}
        fi
        if ls ${NEXELL_TOOLS_FILES_PATH}/secure-bl*.txt 1> /dev/null 2>&1; then
            cp -af ${NEXELL_TOOLS_FILES_PATH}/secure-bl*.txt ${out_dir}
        fi
        if ls ${NEXELL_TOOLS_FILES_PATH}/secure-jtag-hash.txt 1> /dev/null 2>&1; then
            cp -af ${NEXELL_TOOLS_FILES_PATH}/secure-jtag-hash.txt ${out_dir}
        fi
        if ls ${NEXELL_TOOLS_FILES_PATH}/efuse_cfg-*.txt 1> /dev/null 2>&1; then
            cp -af ${NEXELL_TOOLS_FILES_PATH}/efuse_cfg-*.txt ${out_dir}
        fi
    else
        # step1 : copy scripts & tools
        mkdir -p ${out_dir}/tools
        cp -af ${NEXELL_FUSING_TOOLS_PATH}/standalone-fastboot-download.sh ${out_dir}/tools
        cp -af ${NEXELL_FUSING_TOOLS_PATH}/standalone-uboot-by-usb-download.sh ${out_dir}/tools
        cp -af ${NEXELL_FUSING_TOOLS_PATH}/usb-downloader ${out_dir}/tools

        # step2 : copy partition info & images
        copy_board_partmap ${out_dir}/tools


        touch ${out_dir}/YOCTO.${BSP_OUTPUT_DIR_NAME}.INFO.DoNotChange
    fi
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
    echo "\033[40;33m in_root_img : '$2' \033[0m"
    echo "\033[40;33m root_partition_size : '$3' \033[0m"
    echo "\033[40;33m user_partition_size : '$4' \033[0m"
    echo "\033[40;33m dst_path : '$5' \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    local img_type=$1 in_root_img=$2 root_partition_size=$3 user_partition_size=$4 dst_path=$5

    #${NEXELL_TOOL_EXT2SIMG} ${in_ext4_img} ${dst_path}/rootfs.img

	rm -rf ${dst_path}/root
	mkdir -p ${dst_path}/root
	tar xjf ${in_root_img} -C ${dst_path}/root

	${NEXELL_TOOL_MAKE_EXT4FS} -s -l ${root_partition_size}  ${dst_path}/rootfs.img ${dst_path}/root

    rm -rf ${dst_path}/userdata
    mkdir -p ${dst_path}/userdata

    ${NEXELL_TOOL_MAKE_EXT4FS} -s -l ${user_partition_size} -b 4K -a user ${dst_path}/userdata.img ${dst_path}/userdata
    echo "userdata partition size : ${user_partition_size}byte"

    if [ ${BSP_TARGET_BOARD_NAME} = "convergence-daudio" ]; then
        rm -rf ${dst_path}/svmdata
        cp -af ${BSP_ROOT_DIR}/vendor/nexell/apps/svm_daemon/data/ ${dst_path}/svmdata
        ${NEXELL_TOOL_MAKE_EXT4FS} -s -l 33554432 -b 4K -a user ${dst_path}/svmdata.img ${dst_path}/svmdata

        # copy image to output directory
        copy_file_to_output ${dst_path}/svmdata.img
    fi

    cp ${NEXELL_FUSING_TOOLS_PATH}/partition.txt ${dst_path}

    # make misc/etc directory
    rm -rf ${dst_path}/misc
    mkdir -p ${dst_path}/misc/etc

    cp ${NEXELL_TOOLS_PATH}/configs/swu_config/swu.private.key ${dst_path}/misc/etc
    cp ${NEXELL_TOOLS_PATH}/configs/swu_config/swu.public.key ${dst_path}/misc/etc
    cp ${NEXELL_TOOLS_PATH}/configs/swu_config/hwrevision ${dst_path}/misc/etc
    cp ${NEXELL_TOOLS_PATH}/configs/swu_config/sw-versions ${dst_path}/misc/etc
    cp ${NEXELL_TOOLS_PATH}/configs/swu_config/swu-sw-description_emmc ${dst_path}
    cp ${NEXELL_TOOLS_PATH}/swu_image.sh ${dst_path}
    cp ${NEXELL_TOOLS_PATH}/swu_hash.py ${dst_path}
    cp ${NEXELL_TOOLS_PATH}/swu-script-postinstall.sh ${dst_path}
    cp ${NEXELL_TOOLS_PATH}/swu-script-preinstall.sh ${dst_path}

    # make misc.img
    ${NEXELL_TOOL_MAKE_EXT4FS} -s -l 16777216 -b 4K -a misc ${dst_path}/misc.img ${dst_path}/misc
    echo "misc partition size : 16777216 byte"

    # make update.swu
    ${NEXELL_TOOL_SWU_IMAGE} -f ${dst_path}/swu-sw-description_emmc \
    -p nxp4330 -k ${dst_path}/misc/etc/swu.private.key \
    -o ${dst_path}/update.swu -d ${dst_path}
    # copy image to output directory
    copy_file_to_output ${dst_path}/rootfs.img
    copy_file_to_output ${dst_path}/userdata.img
    copy_file_to_output ${dst_path}/misc.img
    copy_file_to_output ${dst_path}/update.swu

    copy_rootfs_image ${dst_path} ${BSP_OUTPUT_DIR_PATH}
}

# =================================================
# s5p4418 only
# =================================================
make_fip_loader_usb_image() {
    export PATH=$PATH:/usr/bin
    echo "\033[40;33m ================================================= \033[0m"
    echo "\033[40;33m make_fip_loader_usb_image \033[0m"
    echo "\033[40;33m ------------------------------------------------- \033[0m"
    echo "\033[40;33m ================================================= \033[0m"

    if [ ! -e "${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT}" ]; then
        echo "WARNING: NOT FOUND IMAGE: ${BL2_EMMCBOOT}"
        return
    fi

    if [ ! -e "${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT}" ]; then
        echo "WARNING: NOT FOUND IMAGE: ${DISPATCHER_EMMCBOOT}"
        return
    fi

    if [ ! -e "${DEPLOY_DIR_IMAGE}/${UBOOT_EMMCBOOT}" ]; then
        echo "WARNING: NOT FOUND IMAGE: ${UBOOT_EMMCBOOT}"
        return
    fi

    rm -rf ${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN}
    rm -rf ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}

    # ===========================================
    # For usb download, create usb download image
    # ===========================================
    # setp 1
    dd if=${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=0 bs=1

    # step 2
    dd if=${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=${DISPATCHER_FIP_NONSECURE_USB_BIN_OFFSET} bs=1

    # step 3
    dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=${UBBOT_FIP_NONSECURE_USB_BIN_OFFSET} bs=1

    # step3. nsih-dummy.txt + fileSize + load/start address => nsih-usbdownload.txt
    do_nsihtxtmod ${DEPLOY_DIR_IMAGE} ${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} ${NEXELL_NSIHDUMMYFILE} ${NSIH_LOAD_START_ADDRESS} ${NSIH_LOAD_START_ADDRESS}

    # step4. nsih-usbdownload.bin
    do_nishbingen ${DEPLOY_DIR_IMAGE}

    # step5, create fip-loader-usb.img
    cp ${DEPLOY_DIR_IMAGE}/"nsih-usbdownload.bin" ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}
    dd if=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} >> ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}

    # copy image to results path
    copy_file_to_output ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}
}
