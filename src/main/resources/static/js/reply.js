// async는 함수 선언시에 사용하고, 해당 함수가 비동기 처리를 위한 함수라는 것을 명시
async function get1(bno) {

    // await는 async함수 내에서 비동기 호출하는 부분에 사용하는것.
    const result = await axios.get(`/replies/list/${bno}`)

    // console.log(result)

    return result;

}

async function getList({bno, page, size, goLast}){

    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})

    if(goLast){
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({bno:bno, page:lastPage, size:size})
    }

    return result.data
}