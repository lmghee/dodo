import React, { useCallback, useEffect, useState } from "react";
import styled from "styled-components";
import DiaryCard from "./DiaryCard";
import Masonry from "@mui/lab/Masonry";
import axios from "axios";
import { useSelector } from "react-redux";
import { useInView } from "react-intersection-observer";
import RefreshIcon from "@mui/icons-material/Refresh";

const Div = styled.div`
  display: flex;
  justify-content: center;
  padding: 0 16px;
`;

export default function ManageDiary() {
  const [diaries, setDiaries] = useState([]);
  const [paging, setPaging] = useState({ page: 0, last: false });
  const [loading, setLoading] = useState(false);
  const [ref, inView] = useInView();
  const { user } = useSelector(state => state);
  const listId = user.value.selectedBucketlist.pk;

  // useEffect(() => {
  //   axios
  //     .get(`https://j8b104.p.ssafy.io/api/bucketlists/${listId}/diaries`, {
  //       headers: {
  //         Authorization: `Bearer ${user.value.token}`,
  //       },
  //     })
  //     .then(res => {
  //       const resData = res.data.data;
  //       setDiaries(resData.content);
  //       setLast(resData.last);
  //     })
  //     .catch(err => console.log(err));
  // }, []);

  const getDiaries = useCallback(async () => {
    const params = { params: paging.page };
    setLoading(true);
    axios
      .get(`https://j8b104.p.ssafy.io/api/bucketlists/${listId}/diaries`, {
        params: params,
        headers: {
          Authorization: `Bearer ${user.value.token}`,
        },
      })
      .then(res => {
        console.log("ok");
        const resData = res.data.data;
        setDiaries(pre => [...pre, ...resData.content]);
        setPaging({ page: resData.number, last: resData.last });
      })
      .catch(err => console.log(err));
    setLoading(false);
  }, []);

  useEffect(() => {
    if (inView && !paging.last) {
      getDiaries();
    }
  }, [inView, paging, getDiaries]);

  return (
    diaries.length !== 0 && (
      <Div>
        <Masonry columns={{ xs: 1, md: 2, lg: 3, xl: 4 }} spacing={2}>
          {diaries.map(diary => (
            <DiaryCard diary={diary} key={diary.createdAt} loading="lazy" />
          ))}
        </Masonry>
        <RefreshIcon sx={{ marginTop: "8px" }} ref={ref} />
      </Div>
    )
  );
}