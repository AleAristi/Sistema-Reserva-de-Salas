public class ApiResponse<T>
{
    public bool ok { get; set; }
    public T data { get; set; }
    public string msg { get; set; }
}

