
do_aes_encrypt() {
    echo "================================================="
    echo "do_aes_encrypt"
    echo "================================================="
    echo "out_img = ${1}"
    echo "in_img = ${2}"
    echo "aes_key = ${3}"
    echo "================================================="

    local out_img=%1 in_img=$2 aes_key=$3

    if [ ! -f ${aes_key} ]; then
        echo "${aes_key} not found!"
        return
    fi

    openssl enc -aes-128-ecb -e \
	    -in ${in_img} \
	    -out ${out_img} -p -nosalt \
	    -K  `cat ${aes_key}`
}

do_gen_hash_rsa() {
    echo "================================================="
    echo "gen_hash_rsa"
    echo "================================================="
    echo "in_img = ${1}"
    echo "hash_name = ${2}"
    echo "private_key = ${3}"
    echo "================================================="
    local in_img=${1}
    local hash_name=${2}
    local private_key=${3}

    # generate hash ... skip
    #openssl dgst -sha256 -binary -out ${hash_name} ${in_img}

    # generate sig, pub
    #echo "private key: ${private_key}"
    #echo "src: ${in_img}"
    ${NEXELL_RSA_SIGN_PSS} ${private_key} ${in_img}

    # <output>
    #     ${in_img}.sig
    #     ${in_img}.pub
}

# For NSIH
do_nsihtxtmod() {
    echo "================================================="
    echo "do_nsihtxtmod"
    echo "-------------------------------------------------"
    echo "src_path : '$1'"
    echo "in_img : '$2'"
    echo "dummy_file : '$3'"
    echo "load_address : '$4'"
    echo "start_address : '$5'"
    echo "================================================="
    local src_path=${1}
    local in_img=${2}
    local dummy_file=${3}
    local load_address=${4}
    local start_address=${5}

    ${NEXELL_NSIH_TOOLS_PATH}/"nsihtxtmod.sh" ${NEXELL_NSIHTXTMOD} ${src_path} ${in_img} ${dummy_file} ${load_address} ${start_address}
}

do_nishbingen() {
    echo "================================================="
    echo "do_nsihtxtmod"
    echo "-------------------------------------------------"
    echo "src_path : '$1'"
    echo "================================================="
    local src_path=${1}

    ${NEXELL_NSIH_TOOLS_PATH}/"nsihbingen.sh" ${NEXELL_NSIHBINGEN} ${src_path}
}